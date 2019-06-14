
package vn.com.cowmanager.software;

import domainapp.basics.exceptions.NotPossibleException;
import domainapp.basics.software.DomainAppToolSoftware;

import vn.com.cowmanager.model.arrangement.Cage;
import vn.com.cowmanager.model.arrangement.CageManager;
import vn.com.cowmanager.model.arrangement.Person;
import vn.com.cowmanager.model.arrangement.Facility;
import vn.com.cowmanager.model.arrangement.Cow;
import vn.com.cowmanager.model.business.Customer;
import vn.com.cowmanager.model.business.Provider;
import vn.com.cowmanager.model.report.CowHealthReport;
import vn.com.cowmanager.model.business.ImportManager;
import vn.com.cowmanager.model.business.OutputManager;
import vn.com.cowmanager.model.feeding.FeedingManager;
import vn.com.cowmanager.model.feeding.Food;
import vn.com.cowmanager.model.feeding.Product;
import vn.com.cowmanager.model.feeding.Storage;
import vn.com.cowmanager.model.treatment.Doctor;
import vn.com.cowmanager.model.treatment.Drug;

import vn.com.cowmanager.model.report.CageStatusReport;
import vn.com.cowmanager.model.report.CheckExpReport;
import vn.com.cowmanager.model.report.ProQuantityReport;
import vn.com.cowmanager.model.report.TradingSurveyReport;

public class CowManagementSoftware extends DomainAppToolSoftware {

	// the domain model of software
	private static final Class[] model = { Cage.class, Cow.class, CageManager.class, Person.class, Provider.class,
			Product.class, Food.class, Storage.class, ImportManager.class, FeedingManager.class, Drug.class,
			Doctor.class, Customer.class, OutputManager.class, CowHealthReport.class,
			CageStatusReport.class, TradingSurveyReport.class, CheckExpReport.class, ProQuantityReport.class};

	@Override
	protected Class[] getModel() {
		return model;
	}

	public static void main(String[] args) throws NotPossibleException {
		new CowManagementSoftware().exec(args);
	}

}
