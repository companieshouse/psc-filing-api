package uk.gov.companieshouse.pscfiling.api.service;

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

    //Use to mock out the PscTypeConstants enum class
    static <PscTypeConstants extends Enum<PscTypeConstants>> PscTypeConstants[] addNewEnumValue(
            Class<PscTypeConstants> enumPscTypeConstants) {
        final EnumSet<PscTypeConstants> enumSet = EnumSet.allOf(enumPscTypeConstants);
        final PscTypeConstants[] newValues =
                (PscTypeConstants[]) Array.newInstance(enumPscTypeConstants, enumSet.size() + 1);
        int i = 0;
        for (final PscTypeConstants value : enumSet) {
            newValues[i] = value;
            i++;
        }

        final PscTypeConstants newEnumValue = mock(enumPscTypeConstants);
        newValues[newValues.length - 1] = newEnumValue;

        when(newEnumValue.ordinal()).thenReturn(newValues.length - 1);

        return newValues;
    }
}