package com.cebix.investmenttrackerapp.mappers;

import com.cebix.investmenttrackerapp.datamodel.Stock;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class StockMapperTests {
    @Test
    public void testMapJSONToStock_IfCorrectData() throws IOException {
        String stockJSON = new String(Files.readAllBytes(Paths.get("src/test/resources/mappers/stock.json")));

        Stock stock = StockMapper.mapJSONToStock(stockJSON);

        assertNotNull(stock);

        assertEquals("AAPL", stock.getTicker());
        assertEquals(130.15, stock.getValue());
        assertEquals(LocalDate.of(2023, 1, 9), stock.getDate());
    }

    @Test
    public void testMapJSONToStock_WithIncorrectJSON() {
        String incorrectJSON = "{ ticker: \"AAPL\", }";

        assertThrows(JSONException.class, () -> StockMapper.mapJSONToStock(incorrectJSON));
    }

    @Test
    public void testMapJSONToStock_WithEmptyJSON() {
        String emptyJSON = "{}";

        assertThrows(JSONException.class, () -> StockMapper.mapJSONToStock(emptyJSON));
    }

    @Test
    public void testMapJSONToStock_WithoutRequiredTickerField() {
        String jsonWithoutRequiredFields = "{ \"results\": [{ \"c\": 130.15, \"t\": 1673240400000 }] }";

        assertThrows(JSONException.class, () -> StockMapper.mapJSONToStock(jsonWithoutRequiredFields));
    }

    @Test
    public void testMapJSONToStock_WithJSONArray() {
        String stockJSONArray = "[{\"ticker\": \"AAPL\", \"results\": [{\"c\": 130.15, \"t\": 1673240400000}]}]";

        Stock stock = StockMapper.mapJSONToStock(stockJSONArray);

        assertNotNull(stock);

        assertEquals("AAPL", stock.getTicker());
        assertEquals(130.15, stock.getValue());
        assertEquals(LocalDate.of(2023, 1, 9), stock.getDate());
    }
}
