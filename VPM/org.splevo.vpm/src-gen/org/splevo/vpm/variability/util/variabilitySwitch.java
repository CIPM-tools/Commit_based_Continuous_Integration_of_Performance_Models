/**
 */
package org.splevo.vpm.variability.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.Switch;
import org.splevo.vpm.variability.*;
import org.splevo.vpm.variability.Variant;
import org.splevo.vpm.variability.VariationPoint;
import org.splevo.vpm.variability.VariationPointGroup;
import org.splevo.vpm.variability.VariationPointModel;
import org.splevo.vpm.variability.variabilityPackage;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.splevo.vpm.variability.variabilityPackage
 * @generated
 */
public class variabilitySwitch<T> extends Switch<T> {
    /**
     * The cached model package
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static variabilityPackage modelPackage;

    /**
     * Creates an instance of the switch.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public variabilitySwitch() {
        if (modelPackage == null) {
            modelPackage = variabilityPackage.eINSTANCE;
        }
    }

    /**
     * Checks whether this is a switch for the given package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param ePackage the package in question.
     * @return whether this is a switch for the given package.
     * @generated
     */
    @Override
    protected boolean isSwitchFor(EPackage ePackage) {
        return ePackage == modelPackage;
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    @Override
    protected T doSwitch(int classifierID, EObject theEObject) {
        switch (classifierID) {
        case variabilityPackage.VARIATION_POINT: {
            VariationPoint variationPoint = (VariationPoint) theEObject;
            T result = caseVariationPoint(variationPoint);
            if (result == null)
                result = caseCustomizableNameHaving(variationPoint);
            if (result == null)
                result = caseCustomizableDescriptionHaving(variationPoint);
            if (result == null)
                result = defaultCase(theEObject);
            return result;
        }
        case variabilityPackage.VARIANT: {
            Variant variant = (Variant) theEObject;
            T result = caseVariant(variant);
            if (result == null)
                result = defaultCase(theEObject);
            return result;
        }
        case variabilityPackage.VARIATION_POINT_MODEL: {
            VariationPointModel variationPointModel = (VariationPointModel) theEObject;
            T result = caseVariationPointModel(variationPointModel);
            if (result == null)
                result = defaultCase(theEObject);
            return result;
        }
        case variabilityPackage.VARIATION_POINT_GROUP: {
            VariationPointGroup variationPointGroup = (VariationPointGroup) theEObject;
            T result = caseVariationPointGroup(variationPointGroup);
            if (result == null)
                result = caseCustomizableNameHaving(variationPointGroup);
            if (result == null)
                result = caseCustomizableDescriptionHaving(variationPointGroup);
            if (result == null)
                result = defaultCase(theEObject);
            return result;
        }
        case variabilityPackage.CUSTOMIZABLE_DESCRIPTION_HAVING: {
            CustomizableDescriptionHaving customizableDescriptionHaving = (CustomizableDescriptionHaving) theEObject;
            T result = caseCustomizableDescriptionHaving(customizableDescriptionHaving);
            if (result == null)
                result = defaultCase(theEObject);
            return result;
        }
        case variabilityPackage.CUSTOMIZABLE_NAME_HAVING: {
            CustomizableNameHaving customizableNameHaving = (CustomizableNameHaving) theEObject;
            T result = caseCustomizableNameHaving(customizableNameHaving);
            if (result == null)
                result = defaultCase(theEObject);
            return result;
        }
        default:
            return defaultCase(theEObject);
        }
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Variation Point</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Variation Point</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseVariationPoint(VariationPoint object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Variant</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Variant</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseVariant(Variant object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Variation Point Model</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Variation Point Model</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseVariationPointModel(VariationPointModel object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Variation Point Group</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Variation Point Group</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseVariationPointGroup(VariationPointGroup object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Customizable Description Having</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Customizable Description Having</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseCustomizableDescriptionHaving(CustomizableDescriptionHaving object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Customizable Name Having</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Customizable Name Having</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseCustomizableNameHaving(CustomizableNameHaving object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch, but this is the last case anyway.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject)
     * @generated
     */
    @Override
    public T defaultCase(EObject object) {
        return null;
    }

} //variabilitySwitch
