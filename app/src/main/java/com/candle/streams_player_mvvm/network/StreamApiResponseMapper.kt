package com.candle.streams_player_mvvm.network

import com.candle.streams_player_mvvm.model.Stream
import com.candle.streams_player_mvvm.util.EntityMapper
import javax.inject.Inject

class StreamApiResponseMapper
@Inject
constructor() : EntityMapper<StreamItemResponse, Stream> {
    override fun mapFromEntity(entity: StreamItemResponse): Stream {
        return Stream(
            id = entity.id,
            recording = entity.recording,
            username_from = entity.username_from,
            timestamp = entity.timestamp,
            username_to = entity.username_to
        )
    }

    override fun mapToEntity(domainModel: Stream): StreamItemResponse {
        return StreamItemResponse(
            id = domainModel.id,
            recording = domainModel.recording,
            username_from = domainModel.username_from,
            timestamp = domainModel.timestamp,
            username_to = domainModel.username_to
        )
    }

    fun mapFromEntityList(entities: List<StreamItemResponse>): List<Stream> {
        return entities.map { mapFromEntity(it) }
    }

}
