package uk.gov.companieshouse.pscfiling.api.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.companieshouse.api.model.psc.PscApi;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.pscfiling.api.config.ValidatorConfig;
import uk.gov.companieshouse.pscfiling.api.error.RestExceptionHandler;
import uk.gov.companieshouse.pscfiling.api.exception.FilingResourceNotFoundException;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;
import uk.gov.companieshouse.pscfiling.api.model.entity.Links;
import uk.gov.companieshouse.pscfiling.api.model.entity.PscIndividualFiling;
import uk.gov.companieshouse.pscfiling.api.service.FilingValidationServiceImpl;
import uk.gov.companieshouse.pscfiling.api.service.PscDetailsService;
import uk.gov.companieshouse.pscfiling.api.service.PscIndividualFilingService;
import uk.gov.companieshouse.pscfiling.api.service.TransactionService;

@Tag("app")
@SpringBootTest(classes = {
        ValidationStatusControllerImpl.class,
        FilingValidationServiceImpl.class,
        RestExceptionHandler.class
}, properties = {"feature.flag.transactions.closable=true"})
@EnableWebMvc
@AutoConfigureMockMvc
@ContextConfiguration(classes = {ValidatorConfig.class})
@ComponentScan(basePackages = {
        "uk.gov.companieshouse.pscfiling.api.validator",
        "uk.gov.companieshouse.pscfiling.api.mapper"
})
class ValidationStatusControllerImplValidationIT extends BaseControllerIT {
    private static final String SELF_FRAGMENT =
            "/transactions/" + TRANS_ID + "/persons-with-significant-control/";

    @MockBean
    private PscIndividualFilingService pscIndividualFilingService;
    @MockBean
    private PscDetailsService pscDetailsService;
    @MockBean
    private TransactionService transactionService;
    @MockBean
    private Logger logger;
    @Autowired
    private MockMvc mockMvc;
    private PscIndividualFiling filing;
    @Mock
    private PscApi pscDetails;

    @BeforeEach
    void setUp() throws Exception {
        super.setUp();
        final var self = UriComponentsBuilder.fromUriString(SELF_FRAGMENT)
                .pathSegment(PscTypeConstants.INDIVIDUAL.getValue())
                .pathSegment(FILING_ID)
                .build()
                .toUri();
        final Links links = new Links(self, null);
        filing = PscIndividualFiling.builder()
                .referenceEtag(ETAG)
                .referencePscId(PSC_ID)
                .ceasedOn(CEASED_ON_DATE)
                .links(links)
                .build();
    }

    @Test
    void validateWhenDataValid() throws Exception {
        when(pscIndividualFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.of(filing));
        when(transactionService.getTransaction(TRANS_ID, PASSTHROUGH_HEADER)).thenReturn(
                transaction);
        when(pscDetailsService.getPscDetails(transaction, PSC_ID, PscTypeConstants.INDIVIDUAL,
                PASSTHROUGH_HEADER)).thenReturn(pscDetails);
        when(pscDetails.getNotifiedOn()).thenReturn(CEASED_ON_DATE.minusDays(1));
        when(pscDetails.getEtag()).thenReturn(ETAG);

        mockMvc.perform(get(URL_VALIDATION_STATUS, TRANS_ID, FILING_ID).headers(
                        httpHeaders))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_valid", is(true)))
                .andExpect(jsonPath("$.errors", is(empty())));
    }

    @Test
    void validateWhenPscDetailsNotFound() throws Exception {
        when(pscIndividualFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.of(filing));
        when(transactionService.getTransaction(TRANS_ID, PASSTHROUGH_HEADER)).thenReturn(
                transaction);
        when(pscDetailsService.getPscDetails(transaction, PSC_ID, PscTypeConstants.INDIVIDUAL,
                PASSTHROUGH_HEADER)).thenThrow(
                new FilingResourceNotFoundException("stub PSC not found", null));

        mockMvc.perform(get(URL_VALIDATION_STATUS, TRANS_ID, FILING_ID).headers(
                        httpHeaders))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_valid", is(false)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].error", is("PSC with that reference ID was not found")))
                .andExpect(jsonPath("$.errors[0].location", is("$.reference_psc_id")))
                .andExpect(jsonPath("$.errors[0].type", is("ch:validation")))
                .andExpect(jsonPath("$.errors[0].location_type", is("json-path")));
    }

    @Test
    void validateWhenPscEtagNotMatched() throws Exception {
        when(pscIndividualFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.of(filing));
        when(transactionService.getTransaction(TRANS_ID, PASSTHROUGH_HEADER)).thenReturn(
                transaction);
        when(pscDetailsService.getPscDetails(transaction, PSC_ID, PscTypeConstants.INDIVIDUAL,
                PASSTHROUGH_HEADER)).thenReturn(pscDetails);
        when(pscDetails.getEtag()).thenReturn("different-etag");

        mockMvc.perform(get(URL_VALIDATION_STATUS, TRANS_ID, FILING_ID).headers(
                        httpHeaders))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_valid", is(false)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].error", is("Etag for PSC does not match latest value")))
                .andExpect(jsonPath("$.errors[0].location", is("$.reference_etag")))
                .andExpect(jsonPath("$.errors[0].type", is("ch:validation")))
                .andExpect(jsonPath("$.errors[0].location_type", is("json-path")));
    }

    @Test
    void validateWhenCeasedOnBeforePscNotifiedOn() throws Exception {
        when(pscIndividualFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.of(filing));
        when(transactionService.getTransaction(TRANS_ID, PASSTHROUGH_HEADER)).thenReturn(
                transaction);
        when(pscDetailsService.getPscDetails(transaction, PSC_ID, PscTypeConstants.INDIVIDUAL,
                PASSTHROUGH_HEADER)).thenReturn(pscDetails);
        when(pscDetails.getEtag()).thenReturn(ETAG);
        when(pscDetails.getNotifiedOn()).thenReturn(CEASED_ON_DATE.plusDays(1));

        mockMvc.perform(get(URL_VALIDATION_STATUS, TRANS_ID, FILING_ID).headers(
                        httpHeaders))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_valid", is(false)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].error", is("Ceased on date cannot be before the date the PSC was notified on")))
                .andExpect(jsonPath("$.errors[0].location", is("$.ceased_on")))
                .andExpect(jsonPath("$.errors[0].type", is("ch:validation")))
                .andExpect(jsonPath("$.errors[0].location_type", is("json-path")));
    }

    @Test
    void validateWhenRegisterEntryDateBeforeCeasedOn() throws Exception {
        when(transactionService.getTransaction(TRANS_ID, PASSTHROUGH_HEADER)).thenReturn(
                transaction);
        when(pscDetailsService.getPscDetails(transaction, PSC_ID, PscTypeConstants.INDIVIDUAL,
                PASSTHROUGH_HEADER)).thenReturn(pscDetails);
        when(pscDetails.getEtag()).thenReturn(ETAG);
        when(pscDetails.getNotifiedOn()).thenReturn(CEASED_ON_DATE.minusDays(1));
        PscIndividualFiling invalid =
                PscIndividualFiling.builder(filing).registerEntryDate(CEASED_ON_DATE.minusDays(1)).build();
        when(pscIndividualFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.of(invalid));

        mockMvc.perform(get(URL_VALIDATION_STATUS, TRANS_ID, FILING_ID).headers(
                        httpHeaders))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_valid", is(false)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].error", is("PSC register entry date cannot be before the cessation date")))
                .andExpect(jsonPath("$.errors[0].location", is("$.register_entry_date")))
                .andExpect(jsonPath("$.errors[0].type", is("ch:validation")))
                .andExpect(jsonPath("$.errors[0].location_type", is("json-path")));
    }

    @Test
    void validateWhenPscNotActive() throws Exception {
        when(pscIndividualFilingService.get(FILING_ID, TRANS_ID)).thenReturn(Optional.of(filing));
        when(transactionService.getTransaction(TRANS_ID, PASSTHROUGH_HEADER)).thenReturn(
                transaction);
        when(pscDetailsService.getPscDetails(transaction, PSC_ID, PscTypeConstants.INDIVIDUAL,
                PASSTHROUGH_HEADER)).thenReturn(pscDetails);
        when(pscDetails.getEtag()).thenReturn(ETAG);
        when(pscDetails.getNotifiedOn()).thenReturn(CEASED_ON_DATE.minusDays(1));
        when(pscDetails.getCeasedOn()).thenReturn(CEASED_ON_DATE);

        mockMvc.perform(get(URL_VALIDATION_STATUS, TRANS_ID, FILING_ID).headers(
                        httpHeaders))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_valid", is(false)))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].error", is("PSC is not active as a ceased on date is present")))
                .andExpect(jsonPath("$.errors[0].location", is("$.ceased_on")))
                .andExpect(jsonPath("$.errors[0].type", is("ch:validation")))
                .andExpect(jsonPath("$.errors[0].location_type", is("json-path")));
    }

}