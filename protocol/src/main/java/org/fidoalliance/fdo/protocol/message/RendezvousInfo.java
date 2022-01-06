package org.fidoalliance.fdo.protocol.message;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.LinkedList;
import org.fidoalliance.fdo.protocol.serialization.GenericListSerializer;


@JsonSerialize(using = GenericListSerializer.class)
public class RendezvousInfo extends LinkedList<RendezvousDirective> {

}
