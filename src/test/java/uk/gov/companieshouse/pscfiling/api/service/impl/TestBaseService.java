package uk.gov.companieshouse.pscfiling.api.service.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Array;
import java.util.EnumSet;
import org.mockito.MockedStatic;
import uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants;

public class TestBaseService {

    static final String PASSTHROUGH_HEADER = "passthrough";
    static final String TRANS_ID = "23445657412";
    static final String COMPANY_NUMBER = "12345678";
    static final String FILING_ID = "6332aa6ed28ad2333c3a520a";
    static PscTypeConstants mockedValue;
    static MockedStatic<PscTypeConstants> myMockedEnum;
    static Class<PscTypeConstants> enumPscTypeConstants = PscTypeConstants.class;

    //Use to mock out the PscTypeConstants enum class
    @SuppressWarnings("unchecked")
    static <PscTypeConstants extends Enum<PscTypeConstants>> PscTypeConstants[] addNewEnumValue() {
        final EnumSet<PscTypeConstants> enumSet = EnumSet.allOf((Class<PscTypeConstants>) enumPscTypeConstants);
        final PscTypeConstants[] newValues =
                (PscTypeConstants[]) Array.newInstance(uk.gov.companieshouse.pscfiling.api.model.PscTypeConstants.class, enumSet.size() + 1);
        int i = 0;
        for (final PscTypeConstants value : enumSet) {
            newValues[i] = value;
            i++;
        }

        final PscTypeConstants newEnumValue = mock((Class<PscTypeConstants>) enumPscTypeConstants);
        newValues[newValues.length - 1] = newEnumValue;

        when(newEnumValue.ordinal()).thenReturn(newValues.length - 1);

        return newValues;
    }
}