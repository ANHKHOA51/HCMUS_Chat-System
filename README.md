## 🚀 Getting Started

This project is a **Chat System** developed for the *Java Application Programming* course at **Ho Chi Minh City University of Science (HCMUS)**.

The application allows users to chat through a local network, built using **JavaFX** for the user interface and standard **Java sockets** for communication.

Currently, the project supports both **Windows** and **Linux** operating systems.

### 🧩 Requirements
- JDK 21  
- JavaFX SDK version 21.0.9 (make sure the `lib` folder is properly configured)  
- (Optional) Maven for easier build and run

## ⚙️ How to run
Firstly, you must copy javaFX into lib folder. We have zip files for each OS, just run command below:

### 🪟 Window
```bash
unzip -jo archive/jfx_win.zip "*/lib/*" -d ./lib
```
### 🐧 Linux
```bash
unzip -jo archive/jfx_linux.zip "*/lib/*" -d ./lib
```

### 🛠️ Compile and run
```bash
javac --module-path lib --add-modules javafx.controls,javafx.fxml -d out src/App.java
java --module-path lib --add-modules javafx.controls,javafx.fxml -cp out App
```
## 📁 Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.