package com.smallrig.extension.ext;

import com.smallrig.extension.ExtensionCoordinate;
import com.smallrig.extension.ExtensionPointI;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
public class ExtensionRepository {

    public Map<ExtensionCoordinate, ExtensionPointI> getExtensionRepo() {
        return extensionRepo;
    }

    private Map<ExtensionCoordinate, ExtensionPointI> extensionRepo = new HashMap<>();


}
