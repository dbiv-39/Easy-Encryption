package application.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ResourceBundle;
import java.util.Scanner;
import javax.crypto.SecretKey;
import application.Main;
import application.model.Encryption;
import application.model.Loaders;
import application.model.Users;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

public class EncryptController implements EventHandler<ActionEvent>, Initializable {
	@FXML
	private AnchorPane apParent, ap1, ap2, apChild1, apChild2;
	@FXML
	private HBox buttons;
	@FXML
    private Button encrypt, decrypt, vault, savedKeys, fopen;
    @FXML 
    private Label regFileContent, encryptFileContent, test; 
	private StringBuilder bookText = null;
	private File file = null;
	private boolean isFileOpen = false; 
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		isFileOpen = false;
		apParent.setStyle("-fx-border-color: black; -fx-border-width: 3px 3px 3px 3px");
		apParent.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		buttons.setSpacing(10);
        buttons.setPadding(new Insets(30, 30, 30, 18));
        buttons.getStyleClass().add("hbox");
        ap1.setStyle("-fx-border-color: black; -fx-border-width: 3px 3px 3px 3px");
        ap2.setStyle("-fx-border-color: black; -fx-border-width: 3px 3px 3px 3px");
        apChild1.setStyle("-fx-border-color: black; -fx-border-width: 3px 3px 3px 3px");
        apChild2.setStyle("-fx-border-color: black; -fx-border-width: 3px 3px 3px 3px");        
	}
	 String encryptRes;
	@Override
	public void handle(ActionEvent event) {
		 
		Loaders loader = new Loaders();
		Button button = (Button) event.getSource();
		String buttonText = button.getText();
		
	    if (buttonText.equals("Decrypt")) 
	    	loader.loadSceneDecrypt();
		else if (buttonText.equals("Vault")) 
			loader.loadSceneVault();
		else if (buttonText.equals("Saved Keys")) 
			loader.loadSceneKeys();
		else if (buttonText.equals("Open File")) {
			FileChooser fChooser = new FileChooser();
			file = fChooser.showOpenDialog(Main.primaryStage);
			Scanner scan = null;
			bookText = new StringBuilder();
			
			try {
				scan = new Scanner(new File(file.getAbsolutePath()));
				//System.out.println(file.getName());
				
				while(scan.hasNextLine()) {
					bookText.append(scan.nextLine());
					bookText.append("\n");
			    }
				scan.close();
		    } catch(IOException e) { e.printStackTrace(); }
			
			regFileContent.setText(bookText.toString());
			isFileOpen = true;
	    }
	    
	    if (isFileOpen && buttonText.equals("Encrypt File")) {
	    	try {
	        // " salt " explanation?
		    // https://en.wikipedia.org/wiki/Salt_(cryptography)
	       
	    	Users users = new Users(); 
	    	String doesFileExist = users.doesFileExist("data/login.csv", file.getName());
	    	
	    	if (doesFileExist == null) {
	    		Encryption encryption = new Encryption();
	    		SecureRandom random = new SecureRandom();
	    		byte bytes[] = new byte[20];
	    		random.nextBytes(bytes);
		    
	    		SecretKey secretKey = Encryption.getKeyFromPassword(LoginController.currentPassword, bytes.toString());
	    		encryptRes = encryption.encrypt(bookText.toString(), secretKey);
	    		encryptFileContent.setText(encryptRes);
	    		users.addKeyAndFile("data/login.csv", LoginController.currentUser, 
	    				"," + file.getName() + "," + encryptRes);
	    	} else encryptFileContent.setText(doesFileExist);
            
            //String d = encryption.decrypt(encryptRes, secretKey, 128);
            //test.setText(d);
            } catch(Exception e) { e.printStackTrace(); }
		}
    }
}
