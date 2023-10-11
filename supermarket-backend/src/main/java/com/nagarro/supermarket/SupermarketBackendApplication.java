package com.nagarro.supermarket;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.PropertyConfigurator;
import org.hibernate.collection.internal.PersistentBag;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.nagarro.supermarket.dao.RoleDao;
import com.nagarro.supermarket.dao.UserDao;
import com.nagarro.supermarket.model.Role;
import com.nagarro.supermarket.model.User;

/**
 * @author prernakumari
 * @author rishabhgusain
 * @author rishabhsinghla
 * @author rinkajsolanki
 * @author aakanksha
 * @author swapnil
 * 
 *         Starting point of application. Beans are created here for mapping
 *         etc.
 *
 */
@SpringBootApplication
public class SupermarketBackendApplication {
	
	static {
		PropertyConfigurator.configure("src/main/resources/log4j.xml");
	} 

	public static void main(String[] args) {
		SpringApplication.run(SupermarketBackendApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();

		@SuppressWarnings("rawtypes")
		Converter<PersistentBag, List> persistentBagToListConverter = new AbstractConverter<PersistentBag, List>() {
			@SuppressWarnings("unchecked")
			@Override
			protected List convert(PersistentBag source) {
				return new ArrayList<>(source);
			}
		};

		modelMapper.addConverter(persistentBagToListConverter);

		return modelMapper;
	}

	@Bean
	CommandLineRunner run(RoleDao roleDao, UserDao userDao, PasswordEncoder passwordEncoder) {

		return args -> {
			if (roleDao.findByAuthority("ADMIN").isPresent())
				return;

			Role adminRole = roleDao.save(new Role("ADMIN"));
			roleDao.save(new Role("USER"));

			Set<Role> roles = new HashSet<>();
			roles.add(adminRole);

			User admin = new User(1, "admin", passwordEncoder.encode("Admin@123"), roles, true);

			userDao.save(admin);
		};
	}
}
