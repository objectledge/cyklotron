package net.cyklotron.cms.category.components;

import java.util.Comparator;
import java.util.List;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.util.CmsResourceListTableModel;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceService;
import net.labeo.services.table.TableColumn;
import net.labeo.services.table.TableException;
import net.labeo.services.table.TableModel;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * This component displays lists of hand-prioritzed resources assigned to queried categories.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: HoldingResourceList.java,v 1.1 2005-01-12 20:45:00 pablo Exp $
 */
public class HoldingResourceList
extends DocumentResourceList
{
    public HoldingResourceList(
        ResourceService resourceService,
        CategoryQueryService categoryQueryService)
    {
        super(resourceService, categoryQueryService);
    }

    public BaseResourceListConfiguration createConfig(RunData data)
    throws ProcessingException
    {
        return new HoldingResourceListConfiguration(CmsData.getCmsData(data).getDate());
    }
    
    public String getTableStateName(RunData data)
    {
        return "net.cyklotron.cms.category.prioritized_resource_list";
    }

    /* (non-Javadoc)
     * @see net.cyklotron.cms.modules.components.category.BaseResourceList#getTableModel(net.labeo.services.resource.Resource[], net.cyklotron.cms.category.BaseResourceListConfiguration, net.labeo.webcore.RunData)
     */
    protected TableModel getTableModel(
        Resource[] resources,
        BaseResourceListConfiguration config,
        RunData data)
        throws TableException
    {
        return new HoldingCmsResourceListTableModel(resources, config, data);
    }

	// implementation /////////////////////////////////////////////////////////////////////////////
    
	/**
	 * Implementation of Table model for hand weighted CMS resources
	 */
	public class HoldingCmsResourceListTableModel extends CmsResourceListTableModel
	{
		protected HoldingResourceListConfiguration componentConfig;
		protected boolean columnsCreated = false;
		
		public HoldingCmsResourceListTableModel(
			Resource[] array,
			BaseResourceListConfiguration config,
			RunData data)
			throws TableException
		{
			super(array, data.getLocale());
			componentConfig = (HoldingResourceListConfiguration) config;
		}

		public HoldingCmsResourceListTableModel(
			List list,
			BaseResourceListConfiguration config,
			RunData data)
			throws TableException
		{
			super(list, data.getLocale());
			componentConfig = (HoldingResourceListConfiguration) config;
		}

		protected TableColumn[] getColumns(TableColumn[] cols) throws TableException
		{
			TableColumn[] newCols = new TableColumn[cols.length];
			for(int i=0; i<cols.length; i++)
			{
				TableColumn col = cols[i];
				newCols[i] = new TableColumn(col.getName(),
					new HoldingComparator(componentConfig, col.getComparator()),
					new HoldingComparator(componentConfig, col.getReverseComparator()));
			}
			return newCols;
		}
        
        /* (non-Javadoc)
         * @see net.labeo.services.table.TableModel#getColumns()
         */
        public TableColumn[] getColumns()
        {
        	if(!columnsCreated)
        	{
				columns = super.getColumns();
        		try
                {
                    columns = getColumns(columns);
                }
                catch (TableException e)
                {
                	throw new RuntimeException("Cannot create columns", e);
                }
                columnsCreated = true;
        	}
			return columns;
        }

	}
	
	public class HoldingComparator implements Comparator
	{
		private HoldingResourceListConfiguration config;
		private Comparator comparator; 
		
        public HoldingComparator(HoldingResourceListConfiguration config, Comparator comparator)
        {
        	this.config = config;
			this.comparator = comparator;
        }

        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object o1, Object o2)
        {
            Resource r1 = (Resource) o1;
			Resource r2 = (Resource) o2;
			if(config.hasResource(r1))
			{
				if(config.hasResource(r2))
				{
					// the sorting is reverse
					int result = config.getWeight(r2) - config.getWeight(r1);
					// sort according to weight if weights are different
					if(result != 0)
					{
						return result;
					}
				}
				else
				{
					// treat the weighted resource as lesser one
					return -1;
				}
			}
			else if(config.hasResource(r2))
			{
				// treat the weighted resource as lesser one
				return 1; 
			}
			// no weighted resources - sort normally
            return comparator.compare(o1, o2);
        }
	}	
}
