package yarnandtail.andhow.appconfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import yarnandtail.andhow.*;
import yarnandtail.andhow.PointValue;
import yarnandtail.andhow.load.FixedValueLoader;
import yarnandtail.andhow.name.BasicNamingStrategy;
import yarnandtail.andhow.ProblemReporter;

/**
 *
 * @author eeverman
 */
public class AppConfigCore implements AppConfigValues {
	//User config
	private final ArrayList<PointValue> forcedValues = new ArrayList();
	private final List<Loader> loaders = new ArrayList();
	private final NamingStrategy namingStrategy;
	private final List<String> cmdLineArgs = new ArrayList();
	
	//Internal state
	private final AppConfigDefinition appConfigDef;
	private final AppConfigStructuredValues loadedValues;
	private final List<ConstructionProblem> constructProblems = new ArrayList();
	private final ArrayList<LoaderException> loaderExceptions = new ArrayList();
	private final ArrayList<RequirementProblem> requirementsProblems = new ArrayList();
	
	public AppConfigCore(NamingStrategy naming, List<Loader> loaders, 
			List<Class<? extends ConfigPointGroup>> registeredGroups, 
			String[] cmdLineArgs, List<PointValue> startingValues) throws AppFatalException {
		
		this.namingStrategy = (naming != null)?naming:new BasicNamingStrategy();
		
		if (loaders != null) {
			for (Loader loader : loaders) {
				if (! this.loaders.contains(loader)) {
					this.loaders.add(loader);
				} else {
					constructProblems.add(new ConstructionProblem.DuplicateLoader(loader));
				}
			}
		}
		
		if (startingValues != null) {
			forcedValues.addAll(startingValues);
			forcedValues.trimToSize();
		}
		
		if (cmdLineArgs != null && cmdLineArgs.length > 0) {
			this.cmdLineArgs.addAll(Arrays.asList(cmdLineArgs));
		}

		appConfigDef = AppConfigUtil.doRegisterConfigPoints(registeredGroups, loaders, namingStrategy);
		constructProblems.addAll(appConfigDef.getConstructionProblems());
		
		//
		//If there are ConstructionProblems, we can't continue on to attempt to
		//load values.
		if (constructProblems.size() > 0) {
			throw new AppFatalException(constructProblems);
		}
		

		//Continuing on to load values
		loadedValues = loadValues().getUnmodifiableAppConfigStructuredValues();

		checkForRequiredValues();

		if (requirementsProblems.size() > 0 || loadedValues.hasProblems()) {
			AppFatalException afe = AppConfigUtil.buildFatalException(requirementsProblems, loadedValues);
			
			ProblemReporter pr = new ProblemReporter(afe, appConfigDef);
			pr.print(System.err);
			
			throw AppConfigUtil.buildFatalException(requirementsProblems, loadedValues);
		}
	}

	public List<Class<? extends ConfigPointGroup>> getGroups() {
		return appConfigDef.getGroups();
	}

	public List<ConfigPoint<?>> getPoints() {
		return appConfigDef.getPoints();
	}
	
	@Override
	public boolean isPointPresent(ConfigPoint<?> point) {
		return loadedValues.isPointPresent(point);
	}
	
	@Override
	public <T> T getValue(ConfigPoint<T> point) {
		return loadedValues.getValue(point);
	}
	
	@Override
	public <T> T getEffectiveValue(ConfigPoint<T> point) {
		return loadedValues.getEffectiveValue(point);
	}
	
	private AppConfigStructuredValues loadValues() throws FatalException {
		AppConfigStructuredValuesBuilder existingValues = new AppConfigStructuredValuesBuilder();

		if (forcedValues.size() > 0) {
			FixedValueLoader fvl = new FixedValueLoader(forcedValues);
			loaders.add(0, fvl);
		}

		//LoaderState state = new LoaderState(cmdLineArgs, existingValues, appConfigDef);
		for (Loader loader : loaders) {
			LoaderValues result = loader.load(appConfigDef, cmdLineArgs, existingValues, loaderExceptions);
			existingValues.addValues(result);
		}

		return existingValues;
	}
	

	private void checkForRequiredValues() {
		
		for (ConfigPoint<?> cp : appConfigDef.getPoints()) {
			if (cp.isRequired()) {
				if (getEffectiveValue(cp) == null) {
					requirementsProblems.add(new RequirementProblem(cp));
				}
			}
		}
		
	}
		
}