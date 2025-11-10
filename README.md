# 🚙 Fuel Consumption Calculator

- Discipline: Programming for Mobile Devices
- Teacher: Junio Moreira
- Student: João Augusto Marciano Silva
- Final date: 31/10/2025

## Application operation

### Light mode

| Main Form (Simple) | Main Form (Complete) | Results Screen | API Info Screen |
|:---:|:---:|:---:|:---:|
| <img height="500" alt="Main Form Light" src="https://github.com/user-attachments/assets/ca0209a0-f721-4632-ad62-16cc4e4183b0" /> | <img height="500" alt="Main Form Light (Complete)" src="https://github.com/user-attachments/assets/7e91aebe-4f77-4eae-9bdc-1d40170968de" /> | <img height="500" alt="Results Screen Light" src="https://github.com/user-attachments/assets/a6647749-f252-45c3-ace4-88fb4e23efab" /> | <img height="500" alt="API Info Screen Light" src="https://github.com/user-attachments/assets/4bcfcccf-7ffe-48f2-9c36-0dc1d34a87a6" />
 |

### Dark mode

| Main Form (Simple) | Main Form (Complete) | Results Screen | API Info Screen |
|:---:|:---:|:---:|:---:|
| <img height="500" alt="Main Form Dark" src="https://github.com/user-attachments/assets/ef3693c2-0508-47a3-b86c-1678cb10e6d7" /> | <img height="500" alt="Main Form Dark (Complete)" src="https://github.com/user-attachments/assets/109da08f-8be9-4075-82c1-b9dd8b44e353" /> | <img height="500" alt="Results Screen Dark" src="https://github.com/user-attachments/assets/adfd5163-8af8-4b29-88bf-35f6c52c6985" /> | <img height="500" alt="API Info Screen Dark" src="https://github.com/user-attachments/assets/d99e476c-aefa-4e7e-9092-53d6829feb9b" /> |





### Testing validations

| Empty Field | Invalid Number And Cross-Field |
|:---:|:---:|
| <img height="500" alt="Empty Field Validation" src="https://github.com/user-attachments/assets/ceeaa58e-f4d3-46c8-970b-31d304e9986a" /> | <img height="500" alt="Invalid Number Validation" src="https://github.com/user-attachments/assets/e28ff995-48d8-4e38-9c87-7f831b1fd4fc" /> |

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

[APK](https://github.com/joaomarcianodev/DM-Prova4/blob/main/app/release/MediaDeConsumo-1.1.apk)
