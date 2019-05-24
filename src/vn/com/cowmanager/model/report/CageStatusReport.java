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
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.model.query.Query;
import domainapp.basics.model.query.QueryToolKit;
import domainapp.basics.model.query.Expression.Op;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.MetaConstants;
import domainapp.basics.model.meta.Select;
import domainapp.basics.modules.report.model.meta.Output;
import vn.com.cowmanager.model.arrangement.CageManager;

@DClass(schema = "report", serialisable = false)
public class CageStatusReport {

	private static Integer idCounter = 0;

	@DAttr(name = "id", id = true, auto = true, type = Type.Integer, length = 5, optional = false, mutable = false)
	private Integer id;

	@DAttr(name = "cageStatus", type = Type.String, length = 25, optional = false)
	private String cageStatus;

	@DAttr(name = "cageManagers", type = Type.Collection, optional = false, mutable = false, serialisable = false, filter = @Select(clazz = CageManager.class, attributes = {
			CageManager.A_id, CageManager.A_cage, CageManager.A_numberOfCow, CageManager.A_weightAvg,
			CageManager.A_spaceCondition, CageManager.A_enviCondition, CageManager.A_date, CageManager.A_dayLeft,
			CageManager.A_outStandard, CageManager.A_cageStatusReport , CageManager.A_tradingServeyReport}))
	@DAssoc(ascName = "cageStatusReport-has-cageManager", role = "cageStatusReport", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = CageManager.class, cardMin = 0, cardMax = MetaConstants.CARD_MORE))
	@Output
	private Collection<CageManager> cageManagers;

	@DAttr(name = "numberOfResult", type = Type.Integer, length = 20, auto = true, mutable = false)
	@Output
	private Integer numberOfResult;

	@DOpt(type = DOpt.Type.ObjectFormConstructor)
	@DOpt(type = DOpt.Type.RequiredConstructor)
	public CageStatusReport(@AttrRef("cageStatus") String cageStatus) throws NotPossibleException, DataSourceException {
		this.id = ++idCounter;
		this.cageStatus = cageStatus;
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
		Query q = QueryToolKit.createSearchQuery(dsm, CageManager.class, new String[] { CageManager.A_spaceCondition },
				new Op[] { Op.MATCH }, new Object[] { "%" + cageStatus + "%" });

		Map<Oid, CageManager> result = qrm.getDom().retrieveObjects(CageManager.class, q);

		if (result != null) {
			// update the main output data
			cageManagers = result.values();

			// update other output (if any)
			numberOfResult = cageManagers.size();
		} else {
			// no data found: reset output
			resetOutput();
		}
	}

	/**
	 * @effects reset all output attributes to their initial values
	 */
	private void resetOutput() {
		cageManagers = null;
		numberOfResult = 0;
	}

	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addCageManager(Collection<CageManager> cageManagers) {
		// do nothing
		return false;
	}

	public Integer getId() {
		return id;
	}

	public String getCageStatus() {
		return cageStatus;
	}

	public void setCageStatus(String cageStatus) throws NotPossibleException, DataSourceException {
		boolean doQueryReport = (cageStatus != null && !cageStatus.equals(this.cageStatus));

		this.cageStatus = cageStatus;

		if (doQueryReport) {
			doQueryReport();
		}
	}

	public Integer getNumberOfResult() {
		return numberOfResult;
	}

	public Collection<CageManager> getCageManagers() {
		return cageManagers;
	}

	public void setCageManagers(Collection<CageManager> cageManagers) {
		this.cageManagers = cageManagers;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Report(" + id + cageStatus + ((cageManagers != null) ? "Ok" : "Null") + ")";
	}

}
