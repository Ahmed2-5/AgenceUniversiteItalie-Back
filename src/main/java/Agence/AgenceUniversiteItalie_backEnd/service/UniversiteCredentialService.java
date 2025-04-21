package Agence.AgenceUniversiteItalie_backEnd.service;


import Agence.AgenceUniversiteItalie_backEnd.entity.Credential;
import Agence.AgenceUniversiteItalie_backEnd.entity.UniversiteCredential;
import Agence.AgenceUniversiteItalie_backEnd.repository.CredentialRepository;
import Agence.AgenceUniversiteItalie_backEnd.repository.UniversiteCredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UniversiteCredentialService {

    @Autowired
    private UniversiteCredentialRepository universiteCredentialRepository;

    @Autowired
    private CredentialRepository credentialRepository;


    @Transactional
    public UniversiteCredential addUniversiteCredentialToCredential(Long credentialId, UniversiteCredential universiteCredential) {
        Credential credential = credentialRepository.findById(credentialId)
                .orElseThrow(() -> new RuntimeException("Credential not found with id: " + credentialId));

        universiteCredential.setCredential(credential);
        return universiteCredentialRepository.save(universiteCredential);
    }

    @Transactional
    public void removeUniversiteCredentialFromCredential(Long universiteCredentialId) {
        UniversiteCredential universiteCredential = universiteCredentialRepository.findById(universiteCredentialId)
                .orElseThrow(() -> new RuntimeException("UniversiteCredential not found with id: " + universiteCredentialId));

        universiteCredential.setCredential(null);
        universiteCredentialRepository.delete(universiteCredential);
    }

    public UniversiteCredential getUniversiteCredentialById(Long id) {
        return universiteCredentialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UniversiteCredential not found with id: " + id));
    }

    public UniversiteCredential updateUniversiteCredential(Long id, UniversiteCredential universiteCredentialDetails) {
        UniversiteCredential universiteCredential = universiteCredentialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UniversiteCredential not found with id: " + id));

        universiteCredential.setUniveriste(universiteCredentialDetails.getUniveriste());
        universiteCredential.setEmailUniversite(universiteCredentialDetails.getEmailUniversite());
        universiteCredential.setPasswordUniversite(universiteCredentialDetails.getPasswordUniversite());

        return universiteCredentialRepository.save(universiteCredential);
    }

    public Iterable<UniversiteCredential> getUniversiteCredentialsByCredentialId(Long credentialId) {
        Credential credential = credentialRepository.findById(credentialId)
                .orElseThrow(() -> new RuntimeException("Credential not found with id: " + credentialId));

        return universiteCredentialRepository.findByCredential(credential);
    }
}
