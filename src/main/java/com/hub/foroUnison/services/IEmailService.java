package com.hub.foroUnison.services;

import com.hub.foroUnison.services.dto.EmailDTO;
import jakarta.mail.MessagingException;

public interface IEmailService {

    public void sendMail(EmailDTO emailDTO) throws MessagingException;
}
