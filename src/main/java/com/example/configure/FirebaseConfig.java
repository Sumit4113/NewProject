package com.example.configure;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

	@Value("${FIREBASE_CONFIGS}")
	private String firebaseConfig;

	@PostConstruct
	public void init() {
		try {
			if (firebaseConfig == null || firebaseConfig.trim().isEmpty()) {
				throw new IllegalStateException("FIREBASE_CONFIGS");
			}

			InputStream serviceAccount = new ByteArrayInputStream(firebaseConfig.getBytes(StandardCharsets.UTF_8));

			FirebaseOptions options = new FirebaseOptions.Builder()
					.setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();

			if (FirebaseApp.getApps().isEmpty()) {
				FirebaseApp.initializeApp(options);
				System.out.println("✅ Firebase initialized successfully.");
			}
		} catch (Exception e) {
			System.err.println("❌ Firebase initialization failed:");
			e.printStackTrace();
		}
	}
}
