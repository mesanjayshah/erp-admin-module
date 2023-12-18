package com.mesanjay.admin.event;

import com.mesanjay.admin.model.Admin;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class RegistrationCompleteEvent extends ApplicationEvent {

    private Admin admin;
    private String confirmationUrl;

    public RegistrationCompleteEvent(Admin admin, String confirmationUrl) {
        super(admin);
        this.admin = admin;
        this.confirmationUrl = confirmationUrl;
    }
}
