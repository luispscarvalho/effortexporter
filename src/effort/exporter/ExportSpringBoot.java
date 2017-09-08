package effort.exporter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.repositoryminer.codemetric.direct.ATFD;
import org.repositoryminer.codemetric.direct.LOC;
import org.repositoryminer.codemetric.direct.LVAR;
import org.repositoryminer.codemetric.direct.MAXNESTING;
import org.repositoryminer.codemetric.direct.MLOC;
import org.repositoryminer.codemetric.direct.NOA;
import org.repositoryminer.codemetric.direct.NOAV;
import org.repositoryminer.codemetric.direct.NOM;
import org.repositoryminer.codemetric.direct.PAR;
import org.repositoryminer.codemetric.direct.TCC;
import org.repositoryminer.codemetric.direct.WMC;
import org.repositoryminer.codesmell.direct.BrainClass;
import org.repositoryminer.codesmell.direct.BrainMethod;
import org.repositoryminer.codesmell.direct.DataClass;
import org.repositoryminer.codesmell.direct.GodClass;
import org.repositoryminer.codesmell.direct.LongMethod;
import org.repositoryminer.effort.postprocessing.EffortByIssuesExporter;
import org.repositoryminer.effort.postprocessing.impl.EffortByIssueFilter;
import org.repositoryminer.listener.mining.ConsoleMiningListener;
import org.repositoryminer.mining.RepositoryMiner;
import org.repositoryminer.parser.java.JavaParser;
import org.repositoryminer.persistence.Connection;
import org.repositoryminer.scm.ReferenceType;
import org.repositoryminer.scm.SCMType;

public class ExportSpringBoot {

	private static final int EFFORT_THRESHOLD_SPRINGBOOT = 50;
	private static Properties props = new Properties();

	public static void init() throws IOException {
		FileInputStream input = new FileInputStream("config.properties");
		props.load(input);
	}

	public static void mineSpringBoot() throws Exception {
		Connection conn = Connection.getInstance();
		conn.connect("mongodb://localhost:27017", "visminer_springboot");

		RepositoryMiner miner = new RepositoryMiner(props.getProperty("path.rep") + "spring-boot",
				props.getProperty("path.tmp"), "SpringBoot", "SpringBoot", SCMType.GIT);

		miner.addParser(new JavaParser());
		miner.addReference("master", ReferenceType.BRANCH);
		miner.setMiningListener(new ConsoleMiningListener());

		miner.addDirectCodeMetric(new ATFD());
		miner.addDirectCodeMetric(new LOC());
		miner.addDirectCodeMetric(new LVAR());
		miner.addDirectCodeMetric(new MAXNESTING());
		miner.addDirectCodeMetric(new MLOC());
		miner.addDirectCodeMetric(new NOA());
		miner.addDirectCodeMetric(new NOAV());
		miner.addDirectCodeMetric(new NOM());
		miner.addDirectCodeMetric(new PAR());
		miner.addDirectCodeMetric(new TCC());
		miner.addDirectCodeMetric(new WMC());

		miner.addDirectCodeSmell(new GodClass());
		miner.addDirectCodeSmell(new BrainClass());
		miner.addDirectCodeSmell(new DataClass());

		miner.addDirectCodeSmell(new BrainMethod());
		miner.addDirectCodeSmell(new LongMethod());

		miner.setMiningListener(new ConsoleMiningListener());

		miner.addPostMiningTask(new EffortByIssuesExporter(new EffortByIssueFilter(), EFFORT_THRESHOLD_SPRINGBOOT,
				props.getProperty("path.csv") + "springboot.csv"));
		miner.mine();

		// Repository repository = miner.mine();
		//
		// HostingServiceMiner hsMiner = new
		// HostingServiceMiner(repository.getId(), "spring-projects",
		// "spring-boot",
		// HostingServiceType.GITHUB);
		// hsMiner.setServiceMiningListener(new ConsoleServiceMiningListener());
		// hsMiner.sync(props.getProperty("github.user"),
		// props.getProperty("github.pass"), 8009);
	}

	public static void main(String[] args) throws Exception {
		init();

		mineSpringBoot();
	}

}
