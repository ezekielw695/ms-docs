package com.ezekielwong.ms.docs.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface WebClientService {

    Object sendWorkflow(String caseId, String name, String params)
            throws NoSuchAlgorithmException, IOException, InvalidKeySpecException;
}
