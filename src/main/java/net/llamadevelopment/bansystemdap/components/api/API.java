package net.llamadevelopment.bansystemdap.components.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.llamadevelopment.bansystemdap.components.forms.FormWindows;
import net.llamadevelopment.bansystemdap.components.provider.Provider;

@AllArgsConstructor
@Getter
public class API {

    private final Provider provider;
    private final FormWindows formWindows;

}
