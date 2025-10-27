package com.thirdeye3.usermanager.services.impl;

import com.thirdeye3.usermanager.entities.*;
import com.thirdeye3.usermanager.exceptions.CSVException;
import com.thirdeye3.usermanager.repositories.*;
import com.thirdeye3.usermanager.services.BackupService;
import com.thirdeye3.usermanager.services.EmailService;
import com.thirdeye3.usermanager.utils.TimeManager;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class BackupServiceImpl implements BackupService {

    @Value("${thirdeye.admin.username}")
    private String toEmail;

    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private ThresholdGroupRepository thresholdGroupRepository;
    @Autowired private ThresholdRepository thresholdRepository;
    @Autowired private TelegramChatIdRepository telegramChatIdRepository;
    @Autowired private EmailService emailService;
    @Autowired private TimeManager timeManager;

    private static final Logger logger = LoggerFactory.getLogger(BackupServiceImpl.class);

    @Override
    public String exportAllToZip() {
        logger.info("Exporting all data to ZIP");

        try (ByteArrayOutputStream zipOut = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(zipOut)) {

            addToZip(zos, "USERS.csv", generateUsersCSV());
            addToZip(zos, "ROLES.csv", generateRolesCSV());
            addToZip(zos, "USER_ROLE_MAPPING.csv", generateMappingsCSV());
            addToZip(zos, "THRESHOLD_GROUP.csv", generateThresholdGroupCSV());
            addToZip(zos, "THRESHOLD.csv", generateThresholdCSV());
            addToZip(zos, "TELEGRAM_CHAT_ID.csv", generateTelegramChatIdCSV());

            zos.close();
            byte[] zipBytes = zipOut.toByteArray();
            String fileName = "SYSTEM_BACKUP_" + timeManager.getCurrentTimeString() + ".zip";

            emailService.sendZipToEmail(
                    toEmail,
                    "System Backup - " + timeManager.getCurrentTimeString(),
                    "Attached is the full system backup (Users, Roles, Thresholds, Telegrams, Mappings).",
                    fileName,
                    zipBytes
            );

            return fileName;
        } catch (IOException e) {
            logger.error("Error creating ZIP backup: {}", e.getMessage());
            throw new CSVException("Failed to create ZIP backup: " + e.getMessage());
        }
    }

    private void addToZip(ZipOutputStream zos, String fileName, byte[] csvData) throws IOException {
        ZipEntry entry = new ZipEntry(fileName);
        zos.putNextEntry(entry);
        zos.write(csvData);
        zos.closeEntry();
    }

    private byte[] generateUsersCSV() {
        List<User> users = userRepository.findAll();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(out);
             CSVPrinter csv = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            for (User u : users) {
                csv.printRecord(u.getUserId(), u.getUserName(), u.getFirstName(), u.getLastName(), u.getPhoneNumber(), u.getActive(), u.getFirstLogin());
            }
            csv.flush();
            return out.toByteArray();
        } catch (IOException e) {
            throw new CSVException("Failed to export users CSV: " + e.getMessage());
        }
    }

    private byte[] generateRolesCSV() {
        List<Role> roles = roleRepository.findAll();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(out);
             CSVPrinter csv = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            for (Role role : roles) {
                csv.printRecord(role.getId(), role.getName());
            }
            csv.flush();
            return out.toByteArray();
        } catch (IOException e) {
            throw new CSVException("Failed to export roles CSV: " + e.getMessage());
        }
    }

    private byte[] generateMappingsCSV() {
        List<Object[]> mappings = userRepository.findAllUserRoleMappings();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(out);
             CSVPrinter csv = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            for (Object[] record : mappings) {
                csv.printRecord(record[0], record[1]);
            }
            csv.flush();
            return out.toByteArray();
        } catch (IOException e) {
            throw new CSVException("Failed to export mapping CSV: " + e.getMessage());
        }
    }

    private byte[] generateThresholdGroupCSV() {
        List<ThresholdGroup> groups = thresholdGroupRepository.findAll();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(out);
             CSVPrinter csv = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            for (ThresholdGroup g : groups) {
                csv.printRecord(g.getId(), g.getGroupName(), g.getUser().getUserId(), g.getActive(), g.getAllStocks(), g.getStockList());
            }
            csv.flush();
            return out.toByteArray();
        } catch (IOException e) {
            throw new CSVException("Failed to export threshold group CSV: " + e.getMessage());
        }
    }

    private byte[] generateThresholdCSV() {
        List<Threshold> thresholds = thresholdRepository.findAll();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(out);
             CSVPrinter csv = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            for (Threshold t : thresholds) {
                csv.printRecord(t.getId(), t.getThresholdGroup().getId(), t.getTimeGapInSeconds(), t.getPriceGap(), t.getType());
            }
            csv.flush();
            return out.toByteArray();
        } catch (IOException e) {
            throw new CSVException("Failed to export threshold CSV: " + e.getMessage());
        }
    }

    private byte[] generateTelegramChatIdCSV() {
        List<TelegramChatId> telegrams = telegramChatIdRepository.findAll();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(out);
             CSVPrinter csv = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            for (TelegramChatId t : telegrams) {
                csv.printRecord(t.getId(), t.getWorkType(), t.getChatId(), t.getChatName(), t.getThresholdGroup().getId());
            }
            csv.flush();
            return out.toByteArray();
        } catch (IOException e) {
            throw new CSVException("Failed to export telegram chat ID CSV: " + e.getMessage());
        }
    }

	
}
