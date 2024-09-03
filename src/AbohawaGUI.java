import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class AbohawaGUI extends JFrame {
    private JSONObject weatherData;

    public AbohawaGUI(){

        // GUI setup and title
        super("Abohawa Protidin");

        // end configuration when it is closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // setup for the size of the GUI in pixels
        setSize(450, 650);

        // setup layout loading screen position
        setLocationRelativeTo(null);

        // set layout manager to null for manual control of the GUI component position
        setLayout(null);

        // prevent resizing of the GUI
        setResizable(false);

        addGuiComponents();
    }

    private void addGuiComponents(){
        // search field
        var searchTextField = new JTextField();

        // set the location and size of component
        searchTextField.setBounds(15, 15, 350, 45);

        // change the font style and size
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 25));

        add(searchTextField);



        // weather image
        var weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);

        // temperature text
        var temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));

        // center the text
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        // weather condition description
        var weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0, 405, 450, 36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        // humidity image
        var humidityImage = new JLabel(loadImage("src/assets/humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        // humidity text
        var humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        // windspeed image
        var windspeedImage = new JLabel(loadImage("src/assets/windspeed.png"));
        windspeedImage.setBounds(220, 500, 74, 66);
        add(windspeedImage);

        // windspeed text
        var windspeedText = new JLabel("<html><b>Windspeed</b> 15km/h</html>");
        windspeedText.setBounds(310, 500, 85, 55);
        windspeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windspeedText);

        //search button
        var searchButton = new JButton(loadImage("src/assets/search.png"));

        // change the cursor to a hand icon while hovering around
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 47, 45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get location from user
                String userInput = searchTextField.getText();

                // validate input - remove whitespace to ensure non-empty text
                if(userInput.replaceAll("\\s", "").length() <= 0){
                    return;
                }

                // retrieve weather data
                weatherData = AbohawaApp.getAbohawaData(userInput);

                // update GUI


                // update weather image
                String weatherCondition = (String) weatherData.get("weather_condition");

                // update image
                switch (weatherCondition){
                    case "Clear" -> weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                    case "Cloudy" -> weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                    case "Rain" -> weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                    case "Snow Fall" -> weatherConditionImage.setIcon(loadImage("src/assets/snow.png"));
                    case "Thunderstorm" -> weatherConditionImage.setIcon(loadImage("src/assets/Thunderstorm.png"));
                }

                // update temperature text
                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + " F");

                // update weather condition text
                weatherConditionDesc.setText(weatherCondition);

                // update humidity text
                long humidity = (long) weatherData.get("humidity");
                humidityText.setText(+ humidity + "%");

                // update windspeed text
                double windspeed = (double) weatherData.get("windspeed");
                windspeedText.setText(windspeed + "km/h");
            }
        });
        add(searchButton);
    }

    // create images in our GUI components
    private ImageIcon loadImage(String sourcePath) {
        try {
            // read the image file from given path
            BufferedImage image = ImageIO.read(new File(sourcePath));

            // returns image icon -> canRendered by components
            return new ImageIcon(image);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Could not find rest");
        return null;
    }
}
