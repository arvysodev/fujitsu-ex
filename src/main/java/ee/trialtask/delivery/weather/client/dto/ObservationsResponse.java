package ee.trialtask.delivery.weather.client.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public record ObservationsResponse(

        @JacksonXmlProperty(isAttribute = true, localName = "timestamp")
        Long timestamp,

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "station")
        List<StationDto> stations
) {
}
