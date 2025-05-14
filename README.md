## Arbiträrwellenform-Generator-Software (Arbitrary Waveform Generation (AWG) Software)

_Diese Software simuliert einen Arbiträrwellengenerator und ist geschrieben in JavaFX-Programmiersprache._

_This software simulates Arbitrary Waveform Generator (AWG) and is written in JavaFX programming language._

---



### Waveform Generation
- **Purpose**: Generates the base waveform based on the selected type and parameters.
- **Logic**:
  - Retrieves parameters from `WaveformData`, including number of samples, sampling rate, frequency, amplitude, and phase.
  - Iterates over the specified number of samples to compute waveform values at time `t = i / samplingRate`.
  - Supports multiple waveform types:
    - **Sine**: Generates a sinusoidal wave using the sine function with the specified frequency and phase.
    - **Square**: Produces a square wave by determining if the phase is within the duty cycle, outputting high (1) or low (-1).
    - **Triangle**: Creates a triangular wave using modulo arithmetic to form a linear ramp.
    - **Sawtooth**: Generates a sawtooth wave with a ramp up/down based on the duty cycle.
    - **Custom**: Interpolates user-defined points from CSV files or manual canvas drawing.
  - Applies various effects:
    - **Jitter**: Introduces random time offsets using Gaussian noise to simulate timing imperfections.
    - **Frequency Sweep**: Adjusts frequency linearly or logarithmically from a start frequency to a stop frequency over a specified duration.
    - **Phase Noise**: Adds random phase variations to mimic phase instability.
    - **Harmonic**: Incorporates a second sine wave with user-defined amplitude and frequency.
    - **Drift**: Scales amplitude linearly over time to simulate signal drift.
    - **DC Offset**: Adds a constant value to shift the waveform vertically.
    - **Burst Mode**: Zeros the signal outside the burst duration, defined by the number of cycles and period.
    - **Quantization**: Rounds values to discrete levels based on the selected resolution (e.g., 8-bit yields 256 levels), with optional quantization noise.
  - Ensures output values are clipped to the range `[-1, 1]` to prevent clipping artifacts.

### Modulation 
- **Purpose**: Applies modulation to the base waveform to simulate communication signals.
- **Logic**:
  - Processes the generated waveform and applies one of the following modulation types based on user selection:
    - **Amplitude Modulation (AM)**: Multiplies the waveform by a carrier signal scaled by the modulation index.
    - **Frequency Modulation (FM)**: Integrates the waveform to modulate the phase of a carrier signal.
    - **Phase Modulation (PM)**: Uses the waveform to directly modulate the phase of the carrier.
    - **Frequency Shift Keying (FSK)**: Shifts the carrier frequency based on the waveform amplitude.
    - **Phase Shift Keying (PSK)**: Multiplies the waveform by a cosine function of the carrier phase.
    - **Quadrature Amplitude Modulation (QAM)**: Combines in-phase and quadrature signals if I/Q mode is enabled, otherwise passes the waveform unchanged.
  - Uses carrier frequency and modulation index from `WaveformData`.
  - Returns the modulated waveform, maintaining the same length as the input.

### Noise Generation
- **Purpose**: Adds noise to the waveform to simulate real-world signal imperfections.
- **Logic**:
  - Generates noise based on the selected noise type and amplitude:
    - **White Noise**: Produces uniformly distributed random values scaled by the noise amplitude.
    - **Gaussian Noise**: Generates normally distributed random values for a more natural noise profile.
    - **Custom Noise**: Interpolates user-defined noise points from imported CSV files, scaled by the amplitude.
  - Adds the generated noise to the waveform, ensuring the combined signal remains within `[-1, 1]`.
  - Skips noise addition if the noise type is "None" or amplitude is zero.

### Main Controller
- **Purpose**: Coordinates user inputs, waveform generation, modulation, noise application, and visualization.
- **Logic**:
  - **Initialization**: Sets up UI components (e.g., combo boxes for waveform types, sliders, text fields) and binds them to `WaveformData` properties for real-time updates.
  - **Input Handling**: Listens for changes in UI elements, updating `WaveformData` and triggering waveform updates via a preview mechanism.
  - **Debouncing**: Employs a 200ms delay using `PauseTransition` to prevent excessive redraws during rapid input changes.
  - **Waveform Generation**:
    - Invokes `WaveformGenerator` to create the base waveform based on the selected type and `WaveformData`.
    - Applies modulation through `ModulationManager` if a modulation type other than "None" is selected.
    - Adds noise via `NoiseGenerator` if noise is enabled and amplitude is positive.
  - **Visualization**:
    - Draws a grid on the canvas with labeled axes for time and amplitude.
    - Plots the waveform by mapping its values to canvas coordinates, ensuring accurate scaling.
  - **Custom Waveforms**:
    - Supports importing waveform points from CSV files for custom waveforms.
    - Enables manual waveform drawing on the canvas when the draw toggle is active, storing points for interpolation.
  - **Error Handling**: Displays alerts for invalid inputs (e.g., negative duration) or processing errors (e.g., CSV parsing failures).

### Data Management 
- **Purpose**: Centralizes all simulation parameters in a single model for consistent access.
- **Logic**:
  - Stores parameters such as sampling rate, number of samples, duration, amplitude, frequency, modulation settings, and noise configurations.
  - Provides getter and setter methods to ensure controlled updates.
  - Maintains relationships between parameters, e.g., updating duration when samples change (`duration = samples / samplingRate`).

---

| ![](https://github.com/KMORaza/AWG-Software/blob/main/src/main/screenshot/001.png) | ![](https://github.com/KMORaza/AWG-Software/blob/main/src/main/screenshot/002.png) | ![](https://github.com/KMORaza/AWG-Software/blob/main/src/main/screenshot/003.png) | ![](https://github.com/KMORaza/AWG-Software/blob/main/src/main/screenshot/004.png) | 
|------------------------------------------------------------------------------------|------------------------------------------------------------------------------------|------------------------------------------------------------------------------------|------------------------------------------------------------------------------------|
| ![](https://github.com/KMORaza/AWG-Software/blob/main/src/main/screenshot/005.png) | ![](https://github.com/KMORaza/AWG-Software/blob/main/src/main/screenshot/006.png) | ![](https://github.com/KMORaza/AWG-Software/blob/main/src/main/screenshot/007.png) | ![](https://github.com/KMORaza/AWG-Software/blob/main/src/main/screenshot/008.png) |
| ![](https://github.com/KMORaza/AWG-Software/blob/main/src/main/screenshot/009.png) | ![](https://github.com/KMORaza/AWG-Software/blob/main/src/main/screenshot/010.png) | ![](https://github.com/KMORaza/AWG-Software/blob/main/src/main/screenshot/011.png) | ![](https://github.com/KMORaza/AWG-Software/blob/main/src/main/screenshot/012.png) |
| ![](https://github.com/KMORaza/AWG-Software/blob/main/src/main/screenshot/013.png) | ![](https://github.com/KMORaza/AWG-Software/blob/main/src/main/screenshot/014.png) | ![](https://github.com/KMORaza/AWG-Software/blob/main/src/main/screenshot/015.png) | ![](https://github.com/KMORaza/AWG-Software/blob/main/src/main/screenshot/016.png) |

---
