package org.online.skyjo.object;

import lombok.Getter;

import javax.enterprise.context.ApplicationScoped;
import java.security.SecureRandom;
import java.util.Random;

@ApplicationScoped
@Getter
public class RandomProvider {

	Random random = new SecureRandom();

}
