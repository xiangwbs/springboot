package com.xwbing.service.demo.netty.encodedecode.two;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Model implements Serializable {
    private static final long serialVersionUID = 4663236112565339000L;
    private String modelName;
    private String modelContent;
}
