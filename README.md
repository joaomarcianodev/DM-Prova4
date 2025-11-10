# 🚙 Fuel Consumption Calculator

- Discipline: Programming for Mobile Devices
- Teacher: Junio Moreira
- Student: João Augusto Marciano Silva
- Final date: 31/10/2025

## Application operation

### Light mode

| Main Form (Simple) | Main Form (Complete) | Results Screen | API Info Screen |
|:---:|:---:|:---:|
<img height="500" alt="Main Form Light" src="[PASTE_YOUR_SCREENSHOT_URL_HERE]" />
<img height="500" alt="Results Screen Light" src="[PASTE_YOUR_SCREENSHOT_URL_HERE]" />
<img height="500" alt="API Info Screen Light" src="[PASTE_YOUR_SCREENSHOT_URL_HERE]" />

### Dark mode

| Main Form (Simple) | Main Form (Complete) | Results Screen | API Info Screen |
|:---:|:---:|:---:|
<img height="500" alt="Main Form Dark" src="[PASTE_YOUR_SCREENSHOT_URL_HERE]" />
<img height="500" alt="Results Screen Dark" src="[PASTE_YOUR_SCREENSHOT_URL_HERE]" />
<img height="500" alt="API Info Screen Dark" src="[PASTE_YOUR_SCREENSHOT_URL_HERE]" />

### Testing validations

| Empty Field | Invalid Number | Cross-Field Validation |
|:---:|:---:|:---:|

<img height="500" alt="Empty Field Validation" src="[PASTE_YOUR_SCREENSHOT_URL_HERE]" />
<img height="500" alt="Invalid Number Validation" src="[PASTE_YOUR_SCREENSHOT_URL_HERE]" />
<img height="500" alt="KM Final less than KM Inicial Validation" src="[PASTE_YOUR_SCREENSHOT_URL_HERE]" />

## Features

- Calculates fuel efficiency (km/L) and trip costs.
- Features two distinct modes: "Simple Analysis" and "Complete Analysis".
- Simple Analysis: Calculates km/L based on distance traveled and liters fueled.
- Complete Analysis:
    - Calculates the "Exact Cost" based on the user-input price per liter.
    - Fetches real-time fuel prices (Gasoline, Diesel) from an external API (combustivelapi.com.br).
    - Uses the user's selected fuel type and state (UF) to find the correct price.
    - Calculates an "Estimated Cost" using the live API data for comparison.
    - Provides robust API error handling and gracefully manages missing data (e.g., if the API doesn't provide a price for a specific fuel/UF combo).
- Displays all calculation results on a clear, separate "Results" screen.
- Includes a third "API Info" screen to display all secondary data from the API (data collection date, data source, price analysis, etc.).
- Features a clickable link on the "API Info" screen to open the data source (fonte) in the user's browser.
- Built with a modern, fully declarative UI using Jetpack Compose.
- Implements a full custom theme with support for both system Light and Dark modes.
- Provides per-field form validation (required, numeric, greater than zero, and cross-field checks) with clear error messages under each field.
- Respects system UI bars (status and navigation) for a seamless "edge-to-edge" feel.
- Includes UX enhancements like clickable text labels for radio buttons.


## Download

APK