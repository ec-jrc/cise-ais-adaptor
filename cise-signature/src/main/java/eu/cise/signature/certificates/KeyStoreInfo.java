/*
 * Copyright CISE AIS Adaptor (c) 2018-2019, European Union
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package eu.cise.signature.certificates;

import eu.cise.resources.FileResource;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import static eu.cise.signature.exceptions.ExceptionHandler.safe;

public class KeyStoreInfo {

    private final String name;
    private final String password;
    private final KeyStore keyStore;

    public KeyStoreInfo(String name, String password) {
        this.name = name;
        this.password = password;
        this.keyStore = getKeyStore();
    }

    public PrivateKey findPrivateKey(String keyAlias, String password) {
        return safe(() -> (PrivateKey) getKeyStore().getKey(keyAlias, password.toCharArray()));
    }

    public Certificate[] findCertificateChain(String keyAlias) {
        return safe(() -> getKeyStore().getCertificateChain(keyAlias));
    }

    public X509Certificate findPublicCertificate(String certificateAlias) {
        return safe(() -> (X509Certificate) getKeyStore().getCertificate(certificateAlias));
    }

    public KeyStore getKeyStore() {
        return keyStore == null ? loadKeyStore(name, password) : keyStore;
    }

    private KeyStore loadKeyStore(String name, String password) {
        return safe(() -> {
            KeyStore jks = KeyStore.getInstance("JKS");
            InputStream jksFileStream = new FileResource(name).getStream();
            jks.load(jksFileStream, password.toCharArray());
            return jks;
        });
    }

}
