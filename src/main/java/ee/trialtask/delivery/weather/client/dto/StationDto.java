package ee.trialtask.delivery.weather.client.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.math.BigDecimal;

public record StationDto(

        @JacksonXmlProperty(localName = "name")
        String name,

        @JacksonXmlProperty(localName = "wmocode")
        String wmoCode,

        @JacksonXmlProperty(localName = "airtemperature")
        BigDecimal airTemperature,

        @JacksonXmlProperty(localName = "windspeed")
        BigDecimal windSpeed,

        @JacksonXmlProperty(localName = "phenomenon")
        String phenomenon,

        @JacksonXmlProperty(localName = "observationtime")
        String observationTime
) {
}
