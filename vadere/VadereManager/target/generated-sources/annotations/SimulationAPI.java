package org.vadere.manager.client.traci;

import org.vadere.manager.client.traci.TraCIClientApi;
import org.vadere.manager.TraCISocket;
import org.vadere.manager.traci.commands.TraCIGetCommand;
import org.vadere.manager.traci.commands.TraCISetCommand;
import org.vadere.manager.traci.response.TraCIGetResponse;
import org.vadere.manager.traci.writer.TraCIPacket;
import org.vadere.manager.traci.response.TraCIResponse;
import java.io.IOException;
import java.util.ArrayList;
import org.vadere.util.geometry.shapes.VPoint;
import org.vadere.manager.traci.TraCICmd;
import org.vadere.manager.traci.commandHandler.variables.SimulationVar;

public class SimulationAPI extends TraCIClientApi {
	public SimulationAPI(TraCISocket socket) {
		super(socket, "SimulationAPI");
	}

	public TraCIResponse getTime() throws IOException {
		TraCIPacket p = TraCIGetCommand.build(TraCICmd.GET_SIMULATION_VALUE, SimulationVar.CURR_SIM_TIME.id, "-1");

		socket.sendExact(p);

		return socket.receiveResponse();
	}

	public TraCIResponse setSimConfig() throws IOException {
		TraCIPacket p = TraCISetCommand.build(TraCICmd.SET_SIMULATION_STATE, "-1", SimulationVar.SIM_CONFIG.id, SimulationVar.SIM_CONFIG.type, null);

		socket.sendExact(p);

		return socket.receiveResponse();
	}

	public TraCIResponse getHash(String data) throws IOException {
		TraCIPacket p = TraCIGetCommand.build(TraCICmd.GET_SIMULATION_VALUE, "-1" , SimulationVar.CACHE_HASH.id, SimulationVar.CACHE_HASH.type, data);

		socket.sendExact(p);

		return socket.receiveResponse();
	}

}
