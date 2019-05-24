package vn.com.cowmanager.model.report;

import java.util.Collection;
import java.util.Map;

import domainapp.basics.core.dodm.dsm.DSMBasic;
import domainapp.basics.core.dodm.qrm.QRM;
import domainapp.basics.exceptions.DataSourceException;
import domainapp.basics.exceptions.NotPossibleException;
import domainapp.basics.model.Oid;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.MetaConstants;
import domainapp.basics.model.meta.Select;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.model.query.Query;
import domainapp.basics.model.query.QueryToolKit;
import domainapp.basics.model.query.Expression.Op;
import domainapp.basics.modules.report.model.meta.Output;
import vn.com.cowmanager.model.arrangement.Cow;

@DClass(schema = "report", serialisable = false)
public class CowHealthReport {
	private static Integer idCounter = 0;

	@DAttr(name = "id", id = true, auto = true, type = Type.Integer, length = 5, optional = false, mutable = false)
	private Integer id;

	@DAttr(name = "cowStatus", type = Type.String, length = 25, optional = false)
	private String cowStatus;

	@DAttr(name = "cows", type = Type.Collection, optional = false, mutable = false, serialisable = false, filter = @Select(clazz = Cow.class, attributes = {
			Cow.A_id, Cow.A_dateBorn, Cow.A_ageByWeek, Cow.A_sex, Cow.A_breed, Cow.A_status, Cow.A_weight,
			Cow.A_cage, Cow.A_dateIn, Cow.A_dateForSale, Cow.A_provider, Cow.A_cowHeathReport }))
	@DAssoc(ascName = "cowHeathReport-has-cow", role = "cowHeathReport", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = Cow.class, cardMin = 0, cardMax = MetaConstants.CARD_MORE))
	@Output
	private Collection<Cow> cows;

	@DAttr(name = "numberOfResult", type = Type.Integer, length = 20, auto = true, mutable = false)
	@Output
	private Integer numberOfResult;

	@DOpt(type = DOpt.Type.ObjectFormConstructor)
	@DOpt(type = DOpt.Type.RequiredConstructor)
	public CowHealthReport(@AttrRef("cowStatus") String cowStatus)
			throws NotPossibleException, DataSourceException {
		this.id = ++idCounter;
		this.cowStatus = cowStatus;
		doQueryReport();
	}

	private void doQueryReport() throws NotPossibleException, DataSourceException {
		// the query manager instance

		QRM qrm = QRM.getInstance();

		// create a query to look up Student from the data source
		// and then populate the output attribute (students) with the result
		DSMBasic dsm = qrm.getDsm();

		// TODO: to conserve memory cache the query and only change the query
		// parameter value(s)
		Query q = QueryToolKit.createSearchQuery(dsm, Cow.class, new String[] { Cow.A_status },
				new Op[] { Op.MATCH }, new Object[] { "%" + cowStatus + "%" });

		Map<Oid, Cow> result = qrm.getDom().retrieveObjects(Cow.class, q);

		if (result != null) {
			// update the main output data
			cows = result.values();

			// update other output (if any)
			numberOfResult = cows.size();
		} else {
			// no data found: reset output
			resetOutput();
		}
	}

	/**
	 * @effects reset all output attributes to their initial values
	 */
	private void resetOutput() {
		cows = null;
		numberOfResult = 0;
	}

	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addcows(Collection<Cow> cows) {
		// do nothing
		return false;
	}

	public Integer getId() {
		return id;
	}

	public String getcowStatus() {
		return cowStatus;
	}

	public void setcowStatus(String cowStatus) throws NotPossibleException, DataSourceException {
		boolean doQueryReport = (cowStatus != null && !cowStatus.equals(this.cowStatus));

		this.cowStatus = cowStatus;

		if (doQueryReport) {
			doQueryReport();
		}
	}

	public void setCageStatus(String cageStatus) {

	}

	public Integer getNumberOfResult() {
		return numberOfResult;
	}

	public Collection<Cow> getcows() {
		return cows;
	}

	public void setcows(Collection<Cow> cows) {
		this.cows = cows;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Report(" + id + cowStatus + ((cows != null) ? "Ok" : "Null") + ")";
	}
}
