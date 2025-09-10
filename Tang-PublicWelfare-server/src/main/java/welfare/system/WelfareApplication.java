package welfare.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("welfare.system.mapper")
public class WelfareApplication {

	public static void main(String[] args) {
		SpringApplication.run(WelfareApplication.class, args);
	}

}
