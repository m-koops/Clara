package org.vaadin.teemu.clara;

import java.io.InputStream;

import org.vaadin.teemu.clara.binder.Binder;
import org.vaadin.teemu.clara.binder.BinderException;
import org.vaadin.teemu.clara.inflater.LayoutInflater;
import org.vaadin.teemu.clara.inflater.LayoutInflaterException;
import org.vaadin.teemu.clara.inflater.filter.AttributeFilter;

import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HasComponents;

public class Clara {

    /**
     * Returns a {@link Component} that is read from the XML representation
     * given as {@link InputStream}. If you would like to bind the resulting
     * {@link Component} to a controller object, you should use
     * {@link #create(InputStream, Object, AttributeFilter...)} method instead.
     * 
     * @param xml
     *            XML representation.
     * @return a {@link Component} that is read from the XML representation.
     */
    public static Component create(InputStream xml) {
        return create(xml, null);
    }

    /**
     * Returns a {@link Component} that is read from the XML representation
     * given as {@link InputStream} and binds the resulting {@link Component} to
     * the given {@code controller} object.
     * 
     * <br />
     * <br />
     * Optionally you may also provide {@link AttributeFilter}s to do some
     * modifications (or example localized translations) to any attributes
     * present in the XML representation.
     * 
     * @param xml
     *            XML representation.
     * @param controller
     *            controller object to bind the resulting {@code Component} (
     *            {@code null} allowed).
     * @param attributeFilters
     *            optional {@link AttributeFilter}s to do attribute
     *            modifications.
     * @return a {@link Component} that is read from the XML representation and
     *         bound to the given {@code controller}.
     * 
     * @throws LayoutInflaterException
     *             if an error is encountered during the layout inflation.
     * @throws BinderException
     *             if an error is encountered during the binding.
     */
    public static Component create(InputStream xml, Object controller,
            AttributeFilter... attributeFilters) {
        Binder binder = new Binder();

        // Inflate the XML to a component (tree).
        LayoutInflater inflater = new LayoutInflater();
        if (attributeFilters != null) {
            for (AttributeFilter filter : attributeFilters) {
                inflater.addAttributeFilter(filter);
            }
        }
        Component result = inflater.inflate(xml,
                binder.getAlreadyAssignedFields(controller));

        // Bind to controller.
        binder.bind(result, controller);
        return result;
    }

    /**
     * Returns a {@link Component} that is read from an XML file in the
     * classpath and binds the resulting {@link Component} to the given
     * {@code controller} object.
     * 
     * <br />
     * <br />
     * The filename is given either as a path relative to the class of the
     * {@code controller} object or as an absolute path. For example if you have
     * a {@code MyController.java} and {@code MyController.xml} files in the
     * same package, you can call this method like
     * {@code Clara.create("MyController.xml", new MyController())}.
     * 
     * <br />
     * <br />
     * Optionally you may also provide {@link AttributeFilter}s to do some
     * modifications (or example localized translations) to any attributes
     * present in the XML representation.
     * 
     * @param xmlClassResourceFileName
     *            filename of the XML representation (within classpath, relative
     *            to {@code controller}'s class or absolute path).
     * @param controller
     *            controller object to bind the resulting {@code Component}
     *            (non-{@code null}).
     * @param attributeFilters
     *            optional {@link AttributeFilter}s to do attribute
     *            modifications.
     * @return a {@link Component} that is read from the XML representation and
     *         bound to the given {@code controller}.
     * 
     * @throws LayoutInflaterException
     *             if an error is encountered during the layout inflation.
     * @throws BinderException
     *             if an error is encountered during the binding.
     */
    public static Component create(String xmlClassResourceFileName,
            Object controller, AttributeFilter... attributeFilters) {
        InputStream xml = controller.getClass().getResourceAsStream(
                xmlClassResourceFileName);
        return create(xml, controller, attributeFilters);
    }

    /**
     * Searches the given component hierarchy {@code root} for a
     * {@link Component} with the given {@code componentId} as its {@code id}
     * property (see {@link Component#setId(String)}).
     * 
     * <br />
     * <br />
     * If the given {@code root} is a {@link ComponentContainer}, this method
     * will recursively iterate the component hierarchy in search for the
     * correct {@link Component}. Otherwise if the given {@code root} is a
     * single {@link Component}, only it is checked for its {@code id} value.
     * 
     * @param root
     *            root of a component tree (non-{@code null}).
     * @param componentId
     *            {@code id} of a component to search for (non-{@code null}).
     * @return {@link Component} with a given {@code componentId} as its
     *         {@code id} or {@code null} if no such component is found.
     * @throws IllegalArgumentException
     *             if either of the given parameters is {@code null}.
     * @see Component#setId(String)
     */
    public static Component findComponentById(Component root, String componentId) {
        // Check for null before doing anything.
        if (componentId == null) {
            throw new IllegalArgumentException("Component id must not be null.");
        }
        if (root == null) {
            throw new IllegalArgumentException(
                    "Root component must not be null.");
        }

        // Recursively traverse the whole component tree starting from the given
        // root component.
        if (componentId.equals(root.getId())) {
            return root;
        } else if (root instanceof HasComponents) {
            for (Component c : (HasComponents) root) {
                Component result = findComponentById(c, componentId);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

}
