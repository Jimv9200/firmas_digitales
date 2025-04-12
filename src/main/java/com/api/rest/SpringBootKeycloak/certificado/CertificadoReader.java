package com.api.rest.SpringBootKeycloak.certificado;

import java.io.FileInputStream;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class CertificadoReader {

    public static PublicKey obtenerClavePublica(String rutaCertificado) throws Exception {
        FileInputStream fis = new FileInputStream(rutaCertificado);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate certificado = (X509Certificate) cf.generateCertificate(fis);
        fis.close();

        return certificado.getPublicKey();
    }

    public static String obtenerPropietario(String rutaCertificado) throws Exception {
        FileInputStream fis = new FileInputStream(rutaCertificado);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate certificado = (X509Certificate) cf.generateCertificate(fis);
        fis.close();

        // Obtener el nombre del propietario desde el certificado
        return certificado.getSubjectDN().getName();
    }
}
