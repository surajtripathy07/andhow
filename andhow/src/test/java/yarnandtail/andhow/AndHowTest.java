package yarnandtail.andhow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import yarnandtail.andhow.load.CmdLineLoader;
import yarnandtail.andhow.name.AsIsAliasNamingStrategy;
import yarnandtail.andhow.name.BasicNamingStrategy;

/**
 *
 * @author eeverman
 */
public class AndHowTest extends AndHowTestBase {
	
	String paramFullPath = SimpleParamsWAlias.class.getCanonicalName() + ".";
	BasicNamingStrategy basicNaming = new BasicNamingStrategy();
	List<Loader> loaders = new ArrayList();
	ArrayList<Class<? extends PropertyGroup>> configPtGroups = new ArrayList();
	Map<Property<?>, Object> startVals = new HashMap();
	String[] cmdLineArgsWFullClassName = new String[0];
	String[] cmdLineArgsWExplicitName = new String[0];
	
	@Before
	public void setup() {
		
		loaders.clear();
		loaders.add(new CmdLineLoader());
		
		configPtGroups.clear();
		configPtGroups.add(SimpleParamsWAlias.class);
		
		startVals.clear();
		startVals.put(SimpleParamsWAlias.KVP_BOB, "test");
		startVals.put(SimpleParamsWAlias.KVP_NULL, "not_null");
		startVals.put(SimpleParamsWAlias.FLAG_TRUE, Boolean.FALSE);
		startVals.put(SimpleParamsWAlias.FLAG_FALSE, Boolean.TRUE);
		startVals.put(SimpleParamsWAlias.FLAG_NULL, Boolean.TRUE);
		
		cmdLineArgsWFullClassName = new String[] {
			paramFullPath + "KVP_BOB" + AndHow.KVP_DELIMITER + "test",
			paramFullPath + "KVP_NULL" + AndHow.KVP_DELIMITER + "not_null",
			paramFullPath + "FLAG_TRUE" + AndHow.KVP_DELIMITER + "false",
			paramFullPath + "FLAG_FALSE" + AndHow.KVP_DELIMITER + "true",
			paramFullPath + "FLAG_NULL" + AndHow.KVP_DELIMITER + "true"
		};
		
		cmdLineArgsWExplicitName = new String[] {
			paramFullPath + SimpleParamsWAlias.KVP_BOB.getBaseAliases().get(0) + AndHow.KVP_DELIMITER + "test",
			paramFullPath + SimpleParamsWAlias.KVP_NULL.getBaseAliases().get(0) + AndHow.KVP_DELIMITER + "not_null",
			paramFullPath + SimpleParamsWAlias.FLAG_TRUE.getBaseAliases().get(0) + AndHow.KVP_DELIMITER + "false",
			paramFullPath + SimpleParamsWAlias.FLAG_FALSE.getBaseAliases().get(0) + AndHow.KVP_DELIMITER + "true",
			paramFullPath + SimpleParamsWAlias.FLAG_NULL.getBaseAliases().get(0) + AndHow.KVP_DELIMITER + "true"
		};
		
	}
	
	@Test
	public void testTheTest() {
		//test the test
		assertEquals("yarnandtail.andhow.SimpleParamsWAlias.", paramFullPath);
	}
	
	@Test
	public void testForcingValuesViaAppConfig() {
		
		AndHow.builder().setNamingStrategy(basicNaming)
				.addLoader(new CmdLineLoader())
				.addGroup(SimpleParamsWAlias.class)
				.addForcedValue(SimpleParamsWAlias.KVP_BOB, "test")
				.addForcedValue(SimpleParamsWAlias.KVP_NULL, "not_null")
				.addForcedValue(SimpleParamsWAlias.FLAG_TRUE, Boolean.FALSE)
				.addForcedValue(SimpleParamsWAlias.FLAG_FALSE, Boolean.TRUE)
				.addForcedValue(SimpleParamsWAlias.FLAG_NULL, Boolean.TRUE)
				.reloadForUnitTesting(reloader);
		
		assertEquals("test", SimpleParamsWAlias.KVP_BOB.getValue());
		assertEquals("not_null", SimpleParamsWAlias.KVP_NULL.getValue());
		assertEquals(false, SimpleParamsWAlias.FLAG_TRUE.getValue());
		assertEquals(true, SimpleParamsWAlias.FLAG_FALSE.getValue());
		assertEquals(true, SimpleParamsWAlias.FLAG_NULL.getValue());
		
		
		List<Property<?>> regPts = AndHow.instance().getPoints();
		
		assertTrue(regPts.contains(SimpleParamsWAlias.KVP_BOB));
		assertTrue(regPts.contains(SimpleParamsWAlias.KVP_NULL));
		assertTrue(regPts.contains(SimpleParamsWAlias.FLAG_TRUE));
		assertTrue(regPts.contains(SimpleParamsWAlias.FLAG_FALSE));
		assertTrue(regPts.contains(SimpleParamsWAlias.FLAG_NULL));
	}
	
	@Test
	public void testDefaultValuesViaLoadingWithNoUserValuesSet() {
		
		AndHow.builder().setNamingStrategy(basicNaming)
				.addLoader(new CmdLineLoader())
				.addGroup(SimpleParamsWAlias.class)
				.reloadForUnitTesting(reloader);
		
		assertEquals("bob", SimpleParamsWAlias.KVP_BOB.getValue());
		assertNull(SimpleParamsWAlias.KVP_NULL.getValue());
		assertTrue(SimpleParamsWAlias.FLAG_TRUE.getValue());
		assertFalse(SimpleParamsWAlias.FLAG_FALSE.getValue());
		assertFalse(SimpleParamsWAlias.FLAG_NULL.getValue());
		
		//Test for the presense of the registered param after the reset
		List<Property<?>> regPts = AndHow.instance().getPoints();
		assertTrue(regPts.contains(SimpleParamsWAlias.KVP_BOB));
		assertTrue(regPts.contains(SimpleParamsWAlias.KVP_NULL));
		assertTrue(regPts.contains(SimpleParamsWAlias.FLAG_TRUE));
		assertTrue(regPts.contains(SimpleParamsWAlias.FLAG_FALSE));
		assertTrue(regPts.contains(SimpleParamsWAlias.FLAG_NULL));
	}
	
	@Test
	public void testCmdLineLoaderUsingExplicitBaseName() {

		AndHow.builder()
				.setNamingStrategy(basicNaming)
				.addLoaders(loaders)
				.addGroups(configPtGroups)
				.setCmdLineArgs(cmdLineArgsWExplicitName)
				.reloadForUnitTesting(reloader);
		
		assertEquals("test", SimpleParamsWAlias.KVP_BOB.getValue());
		assertEquals("not_null", SimpleParamsWAlias.KVP_NULL.getValue());
		assertEquals(false, SimpleParamsWAlias.FLAG_TRUE.getValue());
		assertEquals(true, SimpleParamsWAlias.FLAG_FALSE.getValue());
		assertEquals(true, SimpleParamsWAlias.FLAG_NULL.getValue());
	}
	
	@Test
	public void testCmdLineLoaderUsingClassBaseName() {
		AndHow.builder()
				.setNamingStrategy(basicNaming)
				.addLoaders(loaders)
				.addGroups(configPtGroups)
				.setCmdLineArgs(cmdLineArgsWFullClassName)
				.reloadForUnitTesting(reloader);
		
		assertEquals("test", SimpleParamsWAlias.KVP_BOB.getValue());
		assertEquals("not_null", SimpleParamsWAlias.KVP_NULL.getValue());
		assertEquals(false, SimpleParamsWAlias.FLAG_TRUE.getValue());
		assertEquals(true, SimpleParamsWAlias.FLAG_FALSE.getValue());
		assertEquals(true, SimpleParamsWAlias.FLAG_NULL.getValue());
	}
	
	@Test
	public void testBlowingUpWithDuplicateAliases() {
		
		try {

			AndHow.builder()
				.setNamingStrategy(new AsIsAliasNamingStrategy())
				.addLoaders(loaders)
				.addGroups(configPtGroups)
				.addGroup(SimpleParamsWAliasDuplicate.class)
				.reloadForUnitTesting(reloader);
			
			fail();	//The line above should throw an error
		} catch (AppFatalException ce) {
			assertEquals(5, ce.getConstructionProblems().size());
			assertEquals(SimpleParamsWAliasDuplicate.KVP_BOB, ce.getConstructionProblems().get(0).getBadPoint().getPoint());
			assertEquals(SimpleParamsWAliasDuplicate.KVP_NULL, ce.getConstructionProblems().get(1).getBadPoint().getPoint());
			assertEquals(SimpleParamsWAliasDuplicate.FLAG_FALSE, ce.getConstructionProblems().get(2).getBadPoint().getPoint());
			assertEquals(SimpleParamsWAliasDuplicate.FLAG_TRUE, ce.getConstructionProblems().get(3).getBadPoint().getPoint());
			assertEquals(SimpleParamsWAliasDuplicate.FLAG_NULL, ce.getConstructionProblems().get(4).getBadPoint().getPoint());

		}
	}
	
	@Test
	public void testBlowingUpWithDuplicateLoaders() {
		
		try {

			AndHow.builder()
				.setNamingStrategy(new AsIsAliasNamingStrategy())
				.addLoaders(loaders)
				.addLoader(loaders.get(0))
				.addGroups(configPtGroups)
				.reloadForUnitTesting(reloader);
			
			fail();	//The line above should throw an error
		} catch (AppFatalException ce) {
			assertEquals(1, ce.getConstructionProblems().size());
			assertTrue(ce.getConstructionProblems().get(0) instanceof ConstructionProblem.DuplicateLoader);
			
			ConstructionProblem.DuplicateLoader dl = (ConstructionProblem.DuplicateLoader)ce.getConstructionProblems().get(0);
			assertEquals(loaders.get(0), dl.getLoader());
		}
	}
	
	@Test
	public void testCmdLineLoaderMissingRequiredParamShouldThrowAConfigException() {

		try {
				AndHow.builder()
					.setNamingStrategy(basicNaming)
					.addLoaders(loaders)
					.addGroups(configPtGroups)
					.addGroup(SimpleParamsNoAliasRequired.class)
					.setCmdLineArgs(cmdLineArgsWFullClassName)
					.reloadForUnitTesting(reloader);
			
			fail();	//The line above should throw an error
		} catch (AppFatalException ce) {
			assertEquals(2, ce.getRequirementsProblems().size());
			assertEquals(SimpleParamsNoAliasRequired.KVP_NULL, ((RequirementProblem)(ce.getRequirementsProblems().get(0))).getPoint());
			assertEquals(SimpleParamsNoAliasRequired.FLAG_NULL, ((RequirementProblem)(ce.getRequirementsProblems().get(1))).getPoint());
		}
	}
	
	@Test(expected = RuntimeException.class)
	public void testAttemptingToFetchAConfigPointValueBeforeConfigurationShouldThrowARuntimeException() {
		reloader.destroy();
		String shouldFail = SimpleParamsWAlias.KVP_BOB.getValue();
	}
	

}
