CREATE TABLE weather_observations
(
    id uuid PRIMARY KEY,
    station_name VARCHAR(100) NOT NULL,
    wmo_code VARCHAR(32) NOT NULL,
    city VARCHAR(32) NOT NULL,
    air_temperature DECIMAL(5, 2),
    wind_speed DECIMAL(5, 2),
    weather_phenomenon VARCHAR(255),
    observation_timestamp TIMESTAMP NOT NULL,
    imported_at TIMESTAMP NOT NULL
);

CREATE UNIQUE INDEX ux_weather_observations_station_timestamp
    ON weather_observations (wmo_code, observation_timestamp);

CREATE INDEX ix_weather_observations_city_timestamp
    ON weather_observations (city, observation_timestamp);
