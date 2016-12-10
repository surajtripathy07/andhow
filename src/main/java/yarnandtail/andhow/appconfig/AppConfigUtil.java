package yarnandtail.andhow.appconfig;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import yarnandtail.andhow.AppFatalException;
import yarnandtail.andhow.ConfigPoint;
import yarnandtail.andhow.ConfigPointGroup;
import yarnandtail.andhow.ConstructionProblem;
import yarnandtail.andhow.Loader;
import yarnandtail.andhow.LoaderValues;
import yarnandtail.andhow.NamingStrategy;
import yarnandtail.andhow.PointValue;
import yarnandtail.andhow.PointValueProblem;
import yarnandtail.andhow.RequirementProblem;
import yarnandtail.andhow.ValueMapWithContext;

/**
 * Utilities for AppConfiguration
 * @author eeverman
 */
public class AppConfigUtil {
	

	/**
	 * Build a fully populated AppConfigDefinition from the points contained in
	 * the passed Groups, using the NamingStrategy to generate names for each.
	 * 
	 * @param groups The ConfigPointGroups from which to find ConfigPoints.  May be null
	 * @param naming  A naming strategy to use when reading the properties during loading
	 * @return A fully configured instance
	 */
	public static AppConfigDefinition 
		doRegisterConfigPoints(List<Class<? extends ConfigPointGroup>> groups, List<Loader> loaders, NamingStrategy naming) {

		AppConfigDefinition appDef = new AppConfigDefinition();
		
		if (loaders != null) {
			for (Loader loader : loaders) {
				Class<? extends ConfigPointGroup> group = loader.getLoaderConfig();
				if (group != null) {
					
					doRegisterGroup(appDef, group, naming);
					
				}
			}
		}
		
		//null groups is possible - used in testing and possibly early uses before params are created
		if (groups != null) {
			for (Class<? extends ConfigPointGroup> group : groups) {

				doRegisterGroup(appDef, group, naming);
				
			}
		}
		
		return appDef;

	}
		
	protected static void doRegisterGroup(AppConfigDefinition appDef,
			Class<? extends ConfigPointGroup> group, NamingStrategy naming) {

		try {
			List<ConfigPointGroup.NameAndPoint> nameAndPoints = ConfigPointGroup.getConfigPoints(group);
			
			for (ConfigPointGroup.NameAndPoint nameAndPoint : nameAndPoints) {
				NamingStrategy.Naming names = naming.buildNamesFromCanonical(nameAndPoint.point, group, nameAndPoint.canonName);
				appDef.addPoint(group, nameAndPoint.point, names);
			}
			
		} catch (Exception ex) {
			ConstructionProblem.SecurityException se = new ConstructionProblem.SecurityException(
				ex, group);
			appDef.addConstructionProblem(se);
		}

	}
		
	public static void printExceptions(List<? extends Exception> exceptions, PrintStream out) {
		for (Exception ne : exceptions) {
			out.println(ne.getMessage());
		}
	}
	
	public static AppFatalException buildFatalException(ArrayList<RequirementProblem> requirementsProblems,
			ValueMapWithContext loadedValues) {
		
		ArrayList<PointValueProblem> pvps = new ArrayList();
		
		//build list of PointValueProblems
		
		if (loadedValues != null) {
			for (LoaderValues lvs : loadedValues.getAllLoaderValues()) {
				for (PointValue pv : lvs.getValues()) {
					pvps.addAll(pv.getIssues());
				}
			}
		}
		
		return new AppFatalException(
				"Unable to complete application configuration due to problems. " +
				"See the System.err out or the log files for complete details.",
				pvps, requirementsProblems);
		
	}
}
