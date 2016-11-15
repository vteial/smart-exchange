package io.wybis.smartexchange.model

import groovy.transform.Canonical
import groovy.transform.ToString
import groovyx.gaelyk.datastore.Entity
import groovyx.gaelyk.datastore.Ignore
import io.wybis.smartexchange.model.AbstractModel
import io.wybis.smartexchange.model.Branch
import io.wybis.smartexchange.model.Tran
import io.wybis.smartexchange.model.Transfer
import io.wybis.smartexchange.model.User

@Entity(unindexed=false)
@Canonical
@ToString(includeNames=true)
public class TransferReceipt extends AbstractModel {

	static final String ID_KEY = "transferReceiptId"

	String category

	Date date

	String description

	String status

//	long forUserId
//
//	@Ignore
//	User forUser

	long byUserId

	@Ignore
	User byUser

	long branchId

	@Ignore
	Branch branch

	@Ignore
	List<Transfer> trans

	// persistence operations

	void preUpdate(long updateBy) {
		this.updateBy = updateBy
		this.updateTime = new Date()
	}

	void prePersist(long createAndUpdateBy) {
		this.createBy = createAndUpdateBy
		this.updateBy = createAndUpdateBy
		Date now = new Date()
		this.createTime = now;
		this.updateTime = now;
	}

	// domain operations
}
