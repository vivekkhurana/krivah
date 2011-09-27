(ns krivah.applications.requisition.core

)

(deffields 
	{:fields
		[{:name "department", :label "Department", :type "options", :required true},
			{:name "itemrow", :label "Items", :type "composite", :cardinality FIELD_CARDINALITY_UNLIMITED},
			{:name "itemname", :label "Item Name", :type "text", :required true, :parent "itemrow"}, 
			{:name "quantity", :label "Quantity", :type "text", :required true, :parent "itemrow"} ,
			{:name "reason", :label "Reason for requisition", :required true, :parent "itemrow"}]
	}
)
