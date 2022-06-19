package io.bactery.eclc.services.config

case class Config(weather: WeatherConfig)
case class WeatherConfig (apiKey: String)