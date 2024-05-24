package com.cebix.investmenttrackerapp.mappers;

import com.cebix.investmenttrackerapp.datamodel.TechnicalIndicator;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class TechnicalIndicatorMapperTests {
    @Test
    public void testMapJSONToTechnicalIndicator_IfCorrectData() throws IOException {
        String technicalIndicatorJSON = new String(Files.readAllBytes(Paths.get("src/test/resources/mappers/technicalIndicator.json")));

        TechnicalIndicator technicalIndicator = TechnicalIndicatorMapper.mapJSONToTechnicalIndicator(technicalIndicatorJSON);

        assertNotNull(technicalIndicator);

        assertEquals("AAPL", technicalIndicator.getTicker());
        assertEquals(186.7087000000001, technicalIndicator.getValue());
        assertEquals(LocalDate.of(2023, 9, 1), technicalIndicator.getDate());
    }

    @Test
    public void testMapJSONToTechnicalIndicator_WithIncorrectJSON() {
        String incorrectJSON = "{ ticker: \"AAPL\", }";

        assertThrows(JSONException.class, () -> TechnicalIndicatorMapper.mapJSONToTechnicalIndicator(incorrectJSON));
    }

    @Test
    public void testMapJSONToTechnicalIndicator_WithEmptyJSON() {
        String emptyJSON = "{}";

        assertThrows(JSONException.class, () -> TechnicalIndicatorMapper.mapJSONToTechnicalIndicator(emptyJSON));
    }

    @Test
    public void testMapJSONToTechnicalIndicator_WithoutRequiredTickerField() {
        String jsonWithoutRequiredFields = "{ \"results\": { \"values\": [{ \"value\": 186.7087000000001, \"timestamp\": 1693540800000 }] } }";

        assertThrows(JSONException.class, () -> TechnicalIndicatorMapper.mapJSONToTechnicalIndicator(jsonWithoutRequiredFields));
    }

    @Test
    public void testMapJSONToTechnicalIndicator_WithJSONArray() {
        String technicalIndicatorJSONArray = "[{\"results\": {\"underlying\": {\"url\": \"https://api.polygon.io/v2/aggs/ticker/AAPL/range/1/day/1671598800000/1693612800000?limit=235&sort=desc\"}, \"values\": [{\"timestamp\": 1693540800000, \"value\": 186.7087000000001}]}}]";

        TechnicalIndicator technicalIndicator = TechnicalIndicatorMapper.mapJSONToTechnicalIndicator(technicalIndicatorJSONArray);

        assertNotNull(technicalIndicator);

        assertEquals("AAPL", technicalIndicator.getTicker());
        assertEquals(186.7087000000001, technicalIndicator.getValue());
        assertEquals(LocalDate.of(2023, 9, 1), technicalIndicator.getDate());
    }
}
