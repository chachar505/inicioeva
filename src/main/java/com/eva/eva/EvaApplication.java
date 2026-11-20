
package com.eva.eva;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EvaApplication {

	private static Session sshSession;

	public static void main(String[] args) {

		try {
			String sshHost = "54.167.92.138";
			String sshUser = "ec2-user";
			String privateKey = System.getenv("EVA_SSH_KEY");

			if (privateKey == null || privateKey.isBlank()) {
				throw new IllegalStateException(
						"Falta configurar la variable EVA_SSH_KEY"
				);
			}

			JSch jsch = new JSch();
			jsch.addIdentity(privateKey);

			sshSession = jsch.getSession(sshUser, sshHost, 22);

			sshSession.setConfig("StrictHostKeyChecking", "no");

			sshSession.connect(15000);

			sshSession.setPortForwardingL(
					3307,
					"localhost",
					3306
			);

			System.out.println("Tunel SSH conectado correctamente.");

			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				if (sshSession != null && sshSession.isConnected()) {
					sshSession.disconnect();
					System.out.println("Tunel SSH cerrado.");
				}
			}));

			SpringApplication.run(EvaApplication.class, args);

		} catch (Exception e) {
			System.err.println("Error al iniciar el tunel SSH:");
			e.printStackTrace();
			System.exit(1);
		}
	}
}
