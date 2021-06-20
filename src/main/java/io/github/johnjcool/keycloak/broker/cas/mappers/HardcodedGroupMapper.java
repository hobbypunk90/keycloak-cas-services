package io.github.johnjcool.keycloak.broker.cas.mappers;

import org.keycloak.broker.provider.AbstractIdentityProviderMapper;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.provider.ConfigConstants;
import org.keycloak.broker.provider.IdentityBrokerException;
import org.keycloak.models.*;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HardcodedGroupMapper extends AbstractIdentityProviderMapper {
    protected static final List<ProviderConfigProperty> configProperties = new ArrayList<ProviderConfigProperty>();
    private static final Set<IdentityProviderSyncMode> IDENTITY_PROVIDER_SYNC_MODES = new HashSet<>(Arrays.asList(IdentityProviderSyncMode.values()));

    static {
        ProviderConfigProperty property;
        property = new ProviderConfigProperty();
        property.setName("group");
        property.setLabel("Group");
        property.setHelpText("Group to assign the user to. Type the full group path in the textbox.");
        property.setType(ProviderConfigProperty.STRING_TYPE);
        configProperties.add(property);
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    @Override
    public String getDisplayCategory() {
        return "Group Importer";
    }

    @Override
    public String getDisplayType() {
        return "Hardcoded Group";
    }

    public static final String[] COMPATIBLE_PROVIDERS = {ANY_PROVIDER};


    public static final String PROVIDER_ID = "cas-hardcoded-group-idp-mapper";

    @Override
    public boolean supportsSyncMode(IdentityProviderSyncMode syncMode) {
        return IDENTITY_PROVIDER_SYNC_MODES.contains(syncMode);
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String[] getCompatibleProviders() {
        return COMPATIBLE_PROVIDERS;
    }

    @Override
    public void importNewUser(KeycloakSession session, RealmModel realm, UserModel user, IdentityProviderMapperModel mapperModel, BrokeredIdentityContext context) {
        assignUserToGroup(realm, user, mapperModel);
    }

    private void assignUserToGroup(RealmModel realm, UserModel user, IdentityProviderMapperModel mapperModel) {
        String groupName = mapperModel.getConfig().get("group");
        GroupModel group = KeycloakModelUtils.findGroupByPath(realm, groupName);
        if (group == null) throw new IdentityBrokerException("Unable to find group: " + groupName);
        user.joinGroup(group);
    }

    @Override
    public void updateBrokeredUser(KeycloakSession session, RealmModel realm, UserModel user, IdentityProviderMapperModel mapperModel, BrokeredIdentityContext context) {
        assignUserToGroup(realm, user, mapperModel);
    }

    @Override
    public void updateBrokeredUserLegacy(KeycloakSession session, RealmModel realm, UserModel user, IdentityProviderMapperModel mapperModel, BrokeredIdentityContext context) {
    }

    @Override
    public String getHelpText() {
        return "When user is imported from provider, hardcode a group mapping for it.";
    }
}