package org.vaadin.gridflip;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Binder;
import com.vaadin.data.Binder.Binding;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.HtmlRenderer;

@Push
@Theme("mytheme")
public class MyUI extends UI {

	// This is a view with two Grids stacked, i.e. the other one
	// is behind the other Grid. This is simplified example of the
	// logic. 
	public class FlipView extends VerticalLayout implements View {
        boolean grid1Front = true;

        public FlipView() {
	        final Grid<List<String>> grid1 = createGrid();
	        final Grid<List<String>> grid2 = createGrid();

	        // Set custom styles
	        grid1.addStyleName("grid-front");
	        grid2.addStyleName("grid-back");
	        
	        // We just switching style names back and forth
	        Button button = new Button("Flip grids");
	        button.addClickListener(e -> {
	        	if (grid1Front) {
	        		grid1.removeStyleName("grid-front");
	        		grid2.removeStyleName("grid-back");
	    	        grid1.addStyleName("grid-back");
	    	        grid2.addStyleName("grid-front");
	    	        grid1Front = false;	        		
	        	} else {
	        		grid1.removeStyleName("grid-back");
	        		grid2.removeStyleName("grid-front");
	    	        grid1.addStyleName("grid-front");
	    	        grid2.addStyleName("grid-back");
	    	        grid1Front = true;	        		
	        	}
	        });
	        
	        CssLayout layout = new CssLayout();
	        layout.addComponents(grid1,grid2);
	        layout.setSizeFull();
	        
	        addComponents(layout, button);
	        setExpandRatio(layout, 1);
	        setSizeFull();						
		}
	}

	// View with two Grids in tab sheet
	public class TabsView extends VerticalLayout implements View {
		public TabsView() {
	        final Grid<List<String>> grid1 = createGrid();
	        final Grid<List<String>> grid2 = createGrid();
	        
	        TabSheet tabSheet = new TabSheet();
	        tabSheet.addTab(grid1,"Grid 1");
	        tabSheet.addTab(grid2,"Grid 2");
	        tabSheet.setSizeFull();
	   
	        addComponents(tabSheet);
	        setSizeFull();			
		}
	}
	
    @Override
    protected void init(VaadinRequest vaadinRequest) {
    	// Main view has menu and navigation
    	HorizontalLayout mainLayout = new HorizontalLayout();
    	VerticalLayout menuLayout = new VerticalLayout();
    	VerticalLayout layout = new VerticalLayout();
    	Navigator nav = new Navigator(this,layout);
    	nav.addView("tabs", new TabsView());
    	nav.addView("flip", new FlipView());
    	nav.navigateTo("tabs");
        layout.setSizeFull();
        mainLayout.setSizeFull();
        menuLayout.setWidth("100px");
        mainLayout.addComponents(menuLayout,layout);
        
        Button button1 = new Button("Tabs");
        button1.addClickListener(event -> {
        	nav.navigateTo("tabs");
        });
        Button button2 = new Button("Flip");
        button2.addClickListener(event -> {
        	nav.navigateTo("flip");
        });
        
        menuLayout.addComponents(button1,button2);
        mainLayout.setExpandRatio(layout, 1);
        
        setContent(mainLayout);
    }

	private Grid<List<String>> createGrid() {
		// Instead of usig bean, you can use List or HashMap for column values
		// here we use integer value for column id, so List is the simplest case.
		final Grid<List<String>> grid = new Grid<>();

        Binder<List<String>> binder = new Binder<>();
        grid.getEditor().setEnabled(true);
        grid.getEditor().setBinder(binder);
        grid.getEditor().setBuffered(false);

        int week=1;
        for (int i=0;i<65;i++) {
        	final int index = i;
        	// add column with value provider and renderer
        	TextField textField = new TextField();
        	Binding<List<String>, String> binding = binder.forField(textField).bind(list -> list.get(index), (list, value) -> list.set(index, value));
        	if ((i % 5) == 0) {
        		grid.addColumn(list -> list.get(index)).setId(""+i).setCaption("M"+(i/5)).setEditorBinding(binding).setEditable(true);
        	} else {
        		grid.addColumn(list -> list.get(index)).setId(""+i).setCaption("W"+(week)).setEditorBinding(binding).setEditable(true);
        		week++;
        	}
        	// Additional tip: Complexity of determining column widths is O(nm) n = columns, m = rows in cache
        	// If it is possible to set predefined width, Grid renders much faster since complex algorithm is
        	// not run, try it
//        	 grid.addColumn(list -> list.get(index), new HtmlRenderer()).setWidth(100).setCaption("W"+(i+1));
        }

        // One approach to improve performnce of the large Grid view is to
        // add drill down pattern. Here is simplified example of the idea, uncomment
        // the code to see the effect
        grid.prependHeaderRow();
        for (int i=0;i<65;i++) {
        	if (i%5==0) {
        		final int index = i;
        		Button button = new Button("M"+i/5);
        		button.addClickListener(event -> {
        			if (!grid.getColumn(""+(index+1)).isHidden()) {
        				grid.getColumn(""+(index+1)).setHidden(true);
        				grid.getColumn(""+(index+2)).setHidden(true);
        				grid.getColumn(""+(index+3)).setHidden(true);        			
        				grid.getColumn(""+(index+4)).setHidden(true);        			
        			} else {        		
        				grid.getColumn(""+(index+1)).setHidden(false);
        				grid.getColumn(""+(index+2)).setHidden(false);
        				grid.getColumn(""+(index+3)).setHidden(false);
        				grid.getColumn(""+(index+4)).setHidden(false);
        			}
        		});
        		grid.getHeaderRow(0).getCell(""+i).setComponent(button);;
        	} else {
        		grid.getColumn(""+i).setHidden(true);        		
        	}
        }
        grid.setHeaderRowHeight(42);
        Random random = new Random();
        List<List<String>> items = new ArrayList<>();
        for (int j=0;j<1000;j++) {
            final List<String> values = new ArrayList<>();
            int sum = 0;
        	for (int i=0;i<66;i++) {
        		if (i%5 == 0 && i > 0) {
        			values.add(i-5, ""+sum);
        			sum = 0;
        		} else {
        			int number = random.nextInt(10000);
        			values.add(i, ""+number);
        			sum = sum + number;
        		}
        	}
            
        	items.add(values);
        }
        grid.setItems(items);
        grid.setWidth("1200px");
        grid.setHeight("700px");

		return grid;
	}

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
