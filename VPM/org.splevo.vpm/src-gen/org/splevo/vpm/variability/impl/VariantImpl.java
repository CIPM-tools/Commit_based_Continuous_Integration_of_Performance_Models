/**
 */
package org.splevo.vpm.variability.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.featuremodel.Feature;

import org.eclipse.gmt.modisco.java.ASTNode;

import org.splevo.vpm.variability.Variant;
import org.splevo.vpm.variability.VariationPoint;
import org.splevo.vpm.variability.variabilityPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Variant</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.splevo.vpm.variability.impl.VariantImpl#getChildFeature <em>Child Feature</em>}</li>
 *   <li>{@link org.splevo.vpm.variability.impl.VariantImpl#getSoftwareEntities <em>Software Entities</em>}</li>
 *   <li>{@link org.splevo.vpm.variability.impl.VariantImpl#getLeading <em>Leading</em>}</li>
 *   <li>{@link org.splevo.vpm.variability.impl.VariantImpl#getVariantId <em>Variant Id</em>}</li>
 *   <li>{@link org.splevo.vpm.variability.impl.VariantImpl#getVariationPoint <em>Variation Point</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class VariantImpl extends EObjectImpl implements Variant {
	/**
     * The cached value of the '{@link #getChildFeature() <em>Child Feature</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getChildFeature()
     * @generated
     * @ordered
     */
    protected Feature childFeature;

    /**
     * The cached value of the '{@link #getSoftwareEntities() <em>Software Entities</em>}' reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getSoftwareEntities()
     * @generated
     * @ordered
     */
	protected EList<ASTNode> softwareEntities;

	/**
     * The default value of the '{@link #getLeading() <em>Leading</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getLeading()
     * @generated
     * @ordered
     */
	protected static final Boolean LEADING_EDEFAULT = null;

	/**
     * The cached value of the '{@link #getLeading() <em>Leading</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getLeading()
     * @generated
     * @ordered
     */
	protected Boolean leading = LEADING_EDEFAULT;

	/**
     * The default value of the '{@link #getVariantId() <em>Variant Id</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getVariantId()
     * @generated
     * @ordered
     */
	protected static final String VARIANT_ID_EDEFAULT = null;

	/**
     * The cached value of the '{@link #getVariantId() <em>Variant Id</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getVariantId()
     * @generated
     * @ordered
     */
	protected String variantId = VARIANT_ID_EDEFAULT;

	/**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected VariantImpl() {
        super();
    }

	/**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
	protected EClass eStaticClass() {
        return variabilityPackage.Literals.VARIANT;
    }

	/**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Feature getChildFeature() {
        if (childFeature != null && childFeature.eIsProxy()) {
            InternalEObject oldChildFeature = (InternalEObject)childFeature;
            childFeature = (Feature)eResolveProxy(oldChildFeature);
            if (childFeature != oldChildFeature) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, variabilityPackage.VARIANT__CHILD_FEATURE, oldChildFeature, childFeature));
            }
        }
        return childFeature;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Feature basicGetChildFeature() {
        return childFeature;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setChildFeature(Feature newChildFeature) {
        Feature oldChildFeature = childFeature;
        childFeature = newChildFeature;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, variabilityPackage.VARIANT__CHILD_FEATURE, oldChildFeature, childFeature));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EList<ASTNode> getSoftwareEntities() {
        if (softwareEntities == null) {
            softwareEntities = new EObjectResolvingEList<ASTNode>(ASTNode.class, this, variabilityPackage.VARIANT__SOFTWARE_ENTITIES);
        }
        return softwareEntities;
    }

	/**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public Boolean getLeading() {
        return leading;
    }

	/**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setLeading(Boolean newLeading) {
        Boolean oldLeading = leading;
        leading = newLeading;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, variabilityPackage.VARIANT__LEADING, oldLeading, leading));
    }

	/**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public String getVariantId() {
        return variantId;
    }

	/**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setVariantId(String newVariantId) {
        String oldVariantId = variantId;
        variantId = newVariantId;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, variabilityPackage.VARIANT__VARIANT_ID, oldVariantId, variantId));
    }

	/**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public VariationPoint getVariationPoint() {
        if (eContainerFeatureID() != variabilityPackage.VARIANT__VARIATION_POINT) return null;
        return (VariationPoint)eContainer();
    }

	/**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public NotificationChain basicSetVariationPoint(VariationPoint newVariationPoint, NotificationChain msgs) {
        msgs = eBasicSetContainer((InternalEObject)newVariationPoint, variabilityPackage.VARIANT__VARIATION_POINT, msgs);
        return msgs;
    }

	/**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setVariationPoint(VariationPoint newVariationPoint) {
        if (newVariationPoint != eInternalContainer() || (eContainerFeatureID() != variabilityPackage.VARIANT__VARIATION_POINT && newVariationPoint != null)) {
            if (EcoreUtil.isAncestor(this, newVariationPoint))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
            NotificationChain msgs = null;
            if (eInternalContainer() != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newVariationPoint != null)
                msgs = ((InternalEObject)newVariationPoint).eInverseAdd(this, variabilityPackage.VARIATION_POINT__VARIANTS, VariationPoint.class, msgs);
            msgs = basicSetVariationPoint(newVariationPoint, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, variabilityPackage.VARIANT__VARIATION_POINT, newVariationPoint, newVariationPoint));
    }

	/**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case variabilityPackage.VARIANT__VARIATION_POINT:
                if (eInternalContainer() != null)
                    msgs = eBasicRemoveFromContainer(msgs);
                return basicSetVariationPoint((VariationPoint)otherEnd, msgs);
        }
        return super.eInverseAdd(otherEnd, featureID, msgs);
    }

	/**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case variabilityPackage.VARIANT__VARIATION_POINT:
                return basicSetVariationPoint(null, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

	/**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
	public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
        switch (eContainerFeatureID()) {
            case variabilityPackage.VARIANT__VARIATION_POINT:
                return eInternalContainer().eInverseRemove(this, variabilityPackage.VARIATION_POINT__VARIANTS, VariationPoint.class, msgs);
        }
        return super.eBasicRemoveFromContainerFeature(msgs);
    }

	/**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case variabilityPackage.VARIANT__CHILD_FEATURE:
                if (resolve) return getChildFeature();
                return basicGetChildFeature();
            case variabilityPackage.VARIANT__SOFTWARE_ENTITIES:
                return getSoftwareEntities();
            case variabilityPackage.VARIANT__LEADING:
                return getLeading();
            case variabilityPackage.VARIANT__VARIANT_ID:
                return getVariantId();
            case variabilityPackage.VARIANT__VARIATION_POINT:
                return getVariationPoint();
        }
        return super.eGet(featureID, resolve, coreType);
    }

	/**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case variabilityPackage.VARIANT__CHILD_FEATURE:
                setChildFeature((Feature)newValue);
                return;
            case variabilityPackage.VARIANT__SOFTWARE_ENTITIES:
                getSoftwareEntities().clear();
                getSoftwareEntities().addAll((Collection<? extends ASTNode>)newValue);
                return;
            case variabilityPackage.VARIANT__LEADING:
                setLeading((Boolean)newValue);
                return;
            case variabilityPackage.VARIANT__VARIANT_ID:
                setVariantId((String)newValue);
                return;
            case variabilityPackage.VARIANT__VARIATION_POINT:
                setVariationPoint((VariationPoint)newValue);
                return;
        }
        super.eSet(featureID, newValue);
    }

	/**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
	public void eUnset(int featureID) {
        switch (featureID) {
            case variabilityPackage.VARIANT__CHILD_FEATURE:
                setChildFeature((Feature)null);
                return;
            case variabilityPackage.VARIANT__SOFTWARE_ENTITIES:
                getSoftwareEntities().clear();
                return;
            case variabilityPackage.VARIANT__LEADING:
                setLeading(LEADING_EDEFAULT);
                return;
            case variabilityPackage.VARIANT__VARIANT_ID:
                setVariantId(VARIANT_ID_EDEFAULT);
                return;
            case variabilityPackage.VARIANT__VARIATION_POINT:
                setVariationPoint((VariationPoint)null);
                return;
        }
        super.eUnset(featureID);
    }

	/**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
	public boolean eIsSet(int featureID) {
        switch (featureID) {
            case variabilityPackage.VARIANT__CHILD_FEATURE:
                return childFeature != null;
            case variabilityPackage.VARIANT__SOFTWARE_ENTITIES:
                return softwareEntities != null && !softwareEntities.isEmpty();
            case variabilityPackage.VARIANT__LEADING:
                return LEADING_EDEFAULT == null ? leading != null : !LEADING_EDEFAULT.equals(leading);
            case variabilityPackage.VARIANT__VARIANT_ID:
                return VARIANT_ID_EDEFAULT == null ? variantId != null : !VARIANT_ID_EDEFAULT.equals(variantId);
            case variabilityPackage.VARIANT__VARIATION_POINT:
                return getVariationPoint() != null;
        }
        return super.eIsSet(featureID);
    }

	/**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
	public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (leading: ");
        result.append(leading);
        result.append(", variantId: ");
        result.append(variantId);
        result.append(')');
        return result.toString();
    }

} //VariantImpl
