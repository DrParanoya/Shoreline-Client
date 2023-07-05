package com.caspian.client.api.manager;

import com.caspian.client.api.social.SocialRelation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Manages client social relationships by storing the associated player
 * {@link UUID}'s with a {@link SocialRelation} value to the user. Backed by a
 * {@link ConcurrentMap} so getting/setting runs in O(1).
 *
 * @author linus
 * @since 1.0
 *
 * @see UUID
 * @see SocialRelation
 */
public class SocialManager
{
    //
    private final ConcurrentMap<UUID, SocialRelation> relationships =
            new ConcurrentHashMap<>();

    /**
     *
     *
     * @param uuid
     * @param relation
     * @return
     */
    public boolean isRelation(UUID uuid, SocialRelation relation)
    {
        return relationships.get(uuid) == relation;
    }

    /**
     *
     *
     * @param uuid
     * @return
     *
     * @see #isRelation(UUID, SocialRelation)
     */
    public boolean isFriend(UUID uuid)
    {
        return isRelation(uuid, SocialRelation.FRIEND);
    }

    /**
     *
     *
     * @param uuid
     * @param relation
     */
    public void addRelation(UUID uuid, SocialRelation relation)
    {
        final SocialRelation relationship = relationships.get(uuid);
        if (relationship != null)
        {
            relationships.replace(uuid, relation);
            return;
        }
        relationships.put(uuid, relation);
    }

    /**
     *
     *
     * @param uuid
     *
     * @see #addRelation(UUID, SocialRelation)
     */
    public void addFriend(UUID uuid)
    {
        addRelation(uuid, SocialRelation.FRIEND);
    }

    /**
     *
     *
     * @param relation
     * @return
     */
    public Collection<UUID> getRelations(SocialRelation relation)
    {
        final List<UUID> friends = new ArrayList<>();
        for (Map.Entry<UUID, SocialRelation> relationship :
                relationships.entrySet())
        {
            if (relationship.getValue() == relation)
            {
                friends.add(relationship.getKey());
            }
        }
        return friends;
    }

    /**
     *
     *
     * @return
     *
     * @see #getRelations(SocialRelation)
     */
    public Collection<UUID> getFriends()
    {
        return getRelations(SocialRelation.FRIEND);
    }
}
