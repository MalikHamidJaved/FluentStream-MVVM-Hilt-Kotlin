package com.candle.streams_player_mvvm.database

import com.candle.streams_player_mvvm.model.Stream
import com.candle.streams_player_mvvm.util.EntityMapper
import javax.inject.Inject

class CacheMapper
@Inject
constructor() : EntityMapper<StreamCacheEntity, Stream> {
    override fun mapFromEntity(entity: StreamCacheEntity): Stream {
        return Stream(
            id = entity.id,
            recording = entity.recording,
            username_from = entity.username_from,
            timestamp = entity.timestamp,
            username_to = entity.username_to
        )
    }

    override fun mapToEntity(domainModel: Stream): StreamCacheEntity {
        return StreamCacheEntity(
            id = domainModel.id,
            recording = domainModel.recording,
            username_from = domainModel.username_from,
            timestamp = domainModel.timestamp,
            username_to = domainModel.username_to
        )
    }

    fun mapFromEntityList(entities: List<StreamCacheEntity>): List<Stream> {
        return entities.map { mapFromEntity(it) }
    }

}