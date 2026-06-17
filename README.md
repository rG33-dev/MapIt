# MapIt Weather

MapIt is a modern weather companion designed for those who appreciate a minimalist, high-contrast aesthetic. Built with a focus on precision and user interaction, it blends real-time meteorological data with an intuitive map-driven experience, all wrapped in a sleek, monochrome dark vibe that is as easy on the eyes as it is functional.

## The Vision

The philosophy behind MapIt is simple: weather should be informative but never overwhelming. By utilizing a deep black and slate-grey palette, the app eliminates visual noise, allowing critical weather data and sleek animations to take center stage. Whether you are checking your local forecast or scouting the weather for a trip halfway across the globe, MapIt provides a consistent, premium experience.

## Feature Highlights

Live Interactive Mapping
The core of the experience starts with a full-screen Google Maps integration. Instead of typing city names, users can simply navigate the globe and drop a pin anywhere to instantly fetch localized weather data.

Three-Page Information Slider
Data is organized into a fluid, swipable pager that divides information into digestible bites. The first screen offers a snapshot of current conditions with pulsing monochrome animations. The second screen provides a detailed five-day forecast. The third screen dives into technical statistics like atmospheric pressure and wind speed.

Personalized Weather Notes
MapIt goes beyond numbers by allowing you to add personal context to your forecast. You can tap on any upcoming day in the forecast slider to write a quick note, such as a reminder to bring an umbrella for a meeting or a plan for a weekend hike.

Minimalist Motion Design
To maintain the dark vibe, the app features custom-built animations for sun and cloud states. These subtle rotations and scaling effects provide a living feel to the interface without being a distraction.

## Application Flow

When you first open MapIt, you are greeted with the current weather for your last selected location. The interface is optimized for thumb navigation, allowing you to swipe horizontally to transition from the current conditions to the extended forecast and then to the detailed statistics page. If you need to check the weather elsewhere, a quick tap on the map icon opens the globe. Once you select a new point and confirm, the entire app state refreshes with fresh data from the OpenWeatherMap API.

## Technical Foundation

MapIt is built on a modern Android stack using Jetpack Compose for its entirely declarative UI. Under the hood, it leverages Retrofit for clean network requests and a robust ViewModel-driven architecture that ensures data consistency even as you navigate between the map and the main dashboard.

## Visual Preview

Place for Demo GIF
(Insert a screen recording here showing the pager swipe and map selection)

Screenshots
(Insert high-quality screenshots of the Current Weather, Map Picker, and Forecast Note Dialog)

## Getting Started

To get this project running on your local machine, you will need to add your own API keys. Ensure you have a valid Google Maps SDK key in your AndroidManifest and an OpenWeatherMap API key in the WeatherViewModel. Once those are in place, the project is ready to be built and deployed to any modern Android device.
