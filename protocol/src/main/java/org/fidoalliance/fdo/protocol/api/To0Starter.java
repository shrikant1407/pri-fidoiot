package org.fidoalliance.fdo.protocol.api;

import java.io.IOException;
import java.time.Duration;
import org.fidoalliance.fdo.protocol.Config;
import org.fidoalliance.fdo.protocol.LoggerService;
import org.fidoalliance.fdo.protocol.Mapper;
import org.fidoalliance.fdo.protocol.StandardTo0Client;
import org.fidoalliance.fdo.protocol.db.OnboardConfigSupplier;
import org.fidoalliance.fdo.protocol.dispatch.VoucherQueryFunction;
import org.fidoalliance.fdo.protocol.entity.OnboardingConfig;
import org.fidoalliance.fdo.protocol.message.OwnershipVoucher;
import org.fidoalliance.fdo.protocol.message.To0OwnerSign;
import org.fidoalliance.fdo.protocol.message.To0d;
import org.fidoalliance.fdo.protocol.message.To2AddressEntries;

public class To0Starter extends RestApi {

  @Override
  public void doGet() throws Exception {

    LoggerService logger = new LoggerService(To0Starter.class);
    String guid = getLastSegment();
    Thread thread = new Thread(){
      public void run(){
        try {
          logger.info("Triggering TO0 for GUID: " + guid);
          OwnershipVoucher voucher = Config.getWorker(VoucherQueryFunction.class).apply(
              guid);
          StandardTo0Client to0Client = new StandardTo0Client();
          To0OwnerSign ownerSign = new To0OwnerSign();
          To0d to0d = new To0d();
          to0d.setVoucher(voucher);
          to0d.setWaitSeconds(Duration.ofDays(1).toSeconds());

          OnboardingConfig onboardConfig = new OnboardConfigSupplier().get();
          To2AddressEntries addressEntries =
              Mapper.INSTANCE.readValue(onboardConfig.getRvBlob(),To2AddressEntries.class);
          to0Client.setAddressEntries(addressEntries);

          to0d.setWaitSeconds(onboardConfig.getWaitSeconds());
          to0Client.setTo0d(to0d);
          to0Client.run();
          logger.info("TO0 completed for GUID: " + guid);
        } catch (IOException e ) {
          logger.error("TO0 failed for GUID: " + guid);
        }

      }
    };

    thread.start();

  }
}
