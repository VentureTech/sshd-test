/*
 * Copyright (c) Interactive Information R & D (I2RD) LLC.
 * All Rights Reserved.
 *
 * This software is confidential and proprietary information of
 * I2RD LLC ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered
 * into with I2RD.
 */

package net.venturetech.sshd_test;

import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.compression.BuiltinCompressions;
import org.apache.sshd.common.config.keys.KeyUtils;
import org.apache.sshd.common.file.FileSystemFactory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.ServerBuilder;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.password.AcceptAllPasswordAuthenticator;
import org.apache.sshd.server.auth.password.UserAuthPasswordFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.scp.ScpCommandFactory;
import org.apache.sshd.server.session.SessionFactory;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystem;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.nio.file.FileSystems.newFileSystem;

/**
 * Sftp server to demonstrate issue with
 * data being lost on download and extra data being
 * generated on upload.
 *
 * @author Russ Tennant (russ@venturetech.net)
 */
public class SftpServer
{
    private volatile SshServer sshd;
    private Long maxPacketSize = null;

    /**
     * The entry point of application.
     *
     * @param args the input arguments.
     *
     * @throws Exception on error.
     */
    public static void main(String[] args) throws Exception
    {
        SftpServer server = new SftpServer();
//        server.maxPacketSize = 64 * 1024L;
        server.start();
        Thread.sleep(Long.MAX_VALUE);
    }

    /**
     * Instantiates a new SFTP server.
     */
    public SftpServer()
    {
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            if (sshd != null)
            {
                try
                {
                    stop();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }, "SFTP Shutdown Thread"));
    }

    /**
     * Start server.
     *
     * @throws IOException on error
     */
    public void start() throws IOException
    {
        File testDir = new File(System.getProperty("java.io.tmpdir", "/tmp"), "sshd-test");
        if (!testDir.exists() && !testDir.mkdirs())
        {
            System.err.println("Unable to create directory: " + testDir);
            System.exit(1);
        }
        if (!testDir.canWrite())
        {
            System.err.println("Unable to access directory: " + testDir);
            System.exit(1);
        }
        synchronized (this)
        {
            sshd = SshServer.setUpDefaultServer();
        }
        if(maxPacketSize != null)
            sshd.getProperties().put(SftpSubsystem.MAX_PACKET_LENGTH_PROP, maxPacketSize);
//        sshd.setCompressionFactories(Arrays.asList(
//            BuiltinCompressions.delayedZlib,
//            BuiltinCompressions.zlib,
//            BuiltinCompressions.none
//        ));
        sshd.setPort(2222);
        sshd.setHost("localhost");
        sshd.setUserAuthFactories(Arrays.asList(
            UserAuthPasswordFactory.INSTANCE
            //            SshServer.DEFAULT_USER_AUTH_KB_INTERACTIVE_FACTORY,
            //            SshServer.DEFAULT_USER_AUTH_PUBLIC_KEY_FACTORY,
            //            SshServer.DEFAULT_USER_AUTH_GSS_FACTORY
        ));

        FileSystem fileSystem = newFileSystem(testDir.toPath(), SftpServer.class.getClassLoader());
        FileSystemFactory fileSystemFactory = session -> fileSystem;
        sshd.setFileSystemFactory(fileSystemFactory);

        SimpleGeneratorHostKeyProvider hostKey = new SimpleGeneratorHostKeyProvider(new File("sshd_test_ssh_host_key"));
        hostKey.setAlgorithm(KeyUtils.RSA_ALGORITHM);
        sshd.setKeyPairProvider(hostKey);
        sshd.setPasswordAuthenticator(AcceptAllPasswordAuthenticator.INSTANCE);

        final ScpCommandFactory scpCommandFactory = new ScpCommandFactory();
        sshd.setCommandFactory(scpCommandFactory);

        List<NamedFactory<Command>> namedFactoryList = new ArrayList<>();
        namedFactoryList.add(new SftpSubsystemFactory());
        sshd.setSubsystemFactories(namedFactoryList);

        sshd.start();
    }

    /**
     * Stop server.
     *
     * @throws IOException on error.
     */
    public void stop() throws IOException
    {
        SshServer toStop = sshd;
        if (toStop != null && toStop.isOpen())
        {
            System.out.println("Shutting down SFTP");
            toStop.stop();
        }
    }
}
