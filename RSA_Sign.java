package real_estate_digital_signature;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.*;
import java.security.*;

public class RSA_Sign {
    public static void main(String[] args) {
        // Bouncy Castle 보안 프로바이더 추가
        Security.insertProviderAt(new BouncyCastleProvider(), 1);

        try {
            // Alice과 Bob이 각각 키 쌍 생성
            KeyPair aliceKeyPair = generateKeyPair();
            System.out.println("Alice 키 쌍: " + aliceKeyPair);

            KeyPair bobKeyPair = generateKeyPair();
            System.out.println("bob 키 쌍: " + bobKeyPair);

            // 각 사용자의 키를 파일로 저장
            saveKeyToFile(aliceKeyPair.getPrivate(), "alice_private_key.der");
            saveKeyToFile(aliceKeyPair.getPublic(), "alice_public_key.der");
            saveKeyToFile(bobKeyPair.getPrivate(), "bob_private_key.der");
            saveKeyToFile(bobKeyPair.getPublic(), "bob_public_key.der");

            // 테스트할 파일. 상대경로로 지정해줌
            String fileName = "src/DS/Example_contract.pdf";

            // Alice이 메시지에 대한 서명 생성
            byte[] fileContent = readFileContent(fileName);
            byte[] signatureAlice = sign(aliceKeyPair.getPrivate(), fileContent);

            // Bob이 Alice의 서명을 검증
            boolean isVerified = verify(bobKeyPair.getPublic(), fileContent, signatureAlice);

            if (isVerified) {
                System.out.println("서명이 유효합니다.");
            } else {
                System.out.println("서명이 유효하지 않습니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
        keyPairGenerator.initialize(2048, new SecureRandom());
        return keyPairGenerator.generateKeyPair();
    }

    private static void saveKeyToFile(Key key, String fileName) throws Exception {
        byte[] keyBytes = key.getEncoded();
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(keyBytes);
        }
    }

    private static byte[] readFileContent(String fileName) throws Exception {
        try (FileInputStream fis = new FileInputStream(fileName);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            return baos.toByteArray();
        }
    }

    private static byte[] sign(PrivateKey privateKey, byte[] data) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA", "BC");
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }

    private static boolean verify(PublicKey publicKey, byte[] data, byte[] signature) throws Exception {
        Signature verifier = Signature.getInstance("SHA256withRSA", "BC");
        verifier.initVerify(publicKey);
        verifier.update(data);
        return verifier.verify(signature);
    }
}