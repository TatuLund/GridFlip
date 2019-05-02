package org.vaadin.gridflip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.HtmlRenderer;

@Theme("mytheme")
public class MyUI extends UI {

	
	public class FlipView extends VerticalLayout implements View {
        boolean grid1Front = true;

        public FlipView() {
	        final Grid<HashMap<Integer, String>> grid1 = createGrid();
	        final Grid<HashMap<Integer, String>> grid2 = createGrid();

	        
	        grid1.addStyleName("grid-front");
	        grid2.addStyleName("grid-back");
	        
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

	public class TabsView extends VerticalLayout implements View {
		public TabsView() {
	        final Grid<HashMap<Integer, String>> grid1 = createGrid();
	        final Grid<HashMap<Integer, String>> grid2 = createGrid();
	        
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

	private Grid<HashMap<Integer, String>> createGrid() {
		final Grid<HashMap<Integer, String>> grid = new Grid<>();
        for (int i=0;i<52;i++) {
        	final int index = i;
        	grid.addColumn(map -> map.get(index), new HtmlRenderer()).setCaption("W"+(i+1));
        }

        Random random = new Random();
        List<HashMap<Integer, String>> items = new ArrayList<>();
        for (int j=0;j<1000;j++) {
            final HashMap<Integer, String> values = new HashMap<>();
        	for (int i=0;i<52;i++) {
        		values.put(i, "<B>"+random.nextInt(10000)+VaadinIcons.EURO.getHtml()+"</B>");
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
