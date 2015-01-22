/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esa.snap.gui.actions.file;

import com.bc.ceres.core.Assert;
import org.esa.beam.framework.datamodel.Product;
import org.esa.snap.gui.SnapApp;
import org.esa.snap.gui.SnapDialogs;
import org.netbeans.api.progress.ProgressUtils;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.lang.ref.WeakReference;
import java.text.MessageFormat;

/**
 * Action which closes a selected product.
 *
 * @author Norman
 */
@ActionID(
        category = "File",
        id = "org.esa.snap.gui.actions.file.SaveProductAction"
)
@ActionRegistration(
        displayName = "#CTL_SaveProductActionName"
)
@ActionReference(path = "Menu/File", position = 50, separatorBefore = 49)
@NbBundle.Messages({
        "CTL_SaveProductActionName=Save Product"
})
public final class SaveProductAction extends AbstractAction {

    /**
     * Preferences key for save product headers (MPH, SPH) or not
     */
    public static final String PROPERTY_KEY_SAVE_PRODUCT_HEADERS = "save_product_headers";
    /**
     * Preferences key for save product history or not
     */
    public static final String PROPERTY_KEY_SAVE_PRODUCT_HISTORY = "save_product_history";
    /**
     * Preferences key for save product annotations (ADS) or not
     */
    public static final String PROPERTY_KEY_SAVE_PRODUCT_ANNOTATIONS = "save_product_annotations";
    /**
     * Preferences key for incremental mode at save
     */
    public static final String PROPERTY_KEY_SAVE_INCREMENTAL = "save_incremental";

    /**
     * default value for preference save product annotations (ADS) or not
     */
    public static boolean DEFAULT_VALUE_SAVE_PRODUCT_ANNOTATIONS = false;
    /**
     * default value for preference incremental mode at save
     */
    public static final boolean DEFAULT_VALUE_SAVE_INCREMENTAL = true;
    /**
     * default value for preference save product headers (MPH, SPH) or not
     */
    public static final boolean DEFAULT_VALUE_SAVE_PRODUCT_HEADERS = true;
    /**
     * default value for preference save product history (History) or not
     */
    public static final boolean DEFAULT_VALUE_SAVE_PRODUCT_HISTORY = true;


    private final WeakReference<Product> productRef;


    public SaveProductAction(Product products) {
        productRef = new WeakReference<>(products);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        execute();
    }

    public Boolean execute() {
        Product product = productRef.get();
        if (product != null) {
            if (product.getFileLocation() != null) {
                return saveProduct(product);
            } else {
                return new SaveProductAsAction(product).execute();
            }
        }
        return null;
    }

    public Boolean saveProduct(Product product) {

        Assert.notNull(product.getFileLocation());

        final File file = product.getFileLocation();
        if (file.isFile() && !file.canWrite()) {
            SnapDialogs.showWarning(MessageFormat.format("The product\n" +
                                                                 "''{0}''\n" +
                                                                 "exists and cannot be overwritten, because it is read only.\n" +
                                                                 "Please choose another file or remove the write protection.",
                                                         file.getPath()));
            return false;
        }

        SnapApp.getDefault().setStatusBarMessage(MessageFormat.format("Writing product ''{0}'' to {1}...", product.getDisplayName(), file));

        //boolean incremental = true;
        boolean incremental = false;
        SaveProductOperation operation = new SaveProductOperation(product, incremental);
        ProgressUtils.runOffEventThreadWithProgressDialog(operation,
                                                          Bundle.CTL_SaveProductActionName(),
                                                          operation.getProgressHandle(),
                                                          true,
                                                          50,
                                                          1000);

        SnapApp.getDefault().setStatusBarMessage("");

        return operation.getStatus();
    }

}
