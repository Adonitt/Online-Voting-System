package org.example.kqz.services.impls;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.kqz.dtos.votes.VoteRequestDto;
import org.example.kqz.dtos.votes.VoteResponseDto;
import org.example.kqz.entities.CandidatesEntity;
import org.example.kqz.entities.PartyEntity;
import org.example.kqz.entities.UserEntity;
import org.example.kqz.entities.VoteEntity;
import org.example.kqz.exceptions.*;
import org.example.kqz.mappers.VoteMapper;
import org.example.kqz.repositories.CandidatesRepository;
import org.example.kqz.repositories.PartyRepository;
import org.example.kqz.repositories.UserRepository;
import org.example.kqz.repositories.VoteRepository;
import org.example.kqz.services.interfaces.CastVoteService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CastVoteServiceImplementation implements CastVoteService {
    private final PartyRepository partyRepository;
    private final CandidatesRepository candidatesRepository;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final VoteMapper voteMapper;

    @Override
    public VoteResponseDto castVote(VoteRequestDto voteRequestDto) {
        PartyEntity party = partyRepository.findById(voteRequestDto.getParty())
                .orElseThrow(() -> new PartyNotFoundException("Party not found with ID: " + voteRequestDto.getParty()));

        UserEntity user = validateVoteRequest(voteRequestDto, party);

        List<CandidatesEntity> candidates = candidatesRepository.findAllById(voteRequestDto.getCandidates());

        VoteEntity vote = new VoteEntity();
        vote.setUser(user);
        vote.setParty(party);
        vote.setCandidates(candidates);
        vote.setTimeStamp(LocalDateTime.now());

        VoteEntity savedVote = voteRepository.save(vote);

        user.setHasVoted(true);
        userRepository.save(user);
        voteRepository.save(savedVote);

        return voteMapper.toResponseDto(savedVote);
    }

    private UserEntity validateVoteRequest(VoteRequestDto voteRequestDto, PartyEntity party) {
        String userEmail = AuthServiceImplementation.getLoggedInUserEmail();

        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getBirthDate() == null || user.getBirthDate().isAfter(LocalDate.now().minusYears(18))) {
            throw new MustBe18ToVote("You must be 18 or older to vote.");
        }

        if (user.isHasVoted()) {
            throw new AlreadyVotedException("You have already voted.");
        }

        List<Long> candidateIds = voteRequestDto.getCandidates();
        if (candidateIds.size() < 1 || candidateIds.size() > 5) {
            throw new MustChooseBetween1And10Candidates("You must select between 1 and 10 candidates.");
        }

        List<CandidatesEntity> candidates = candidatesRepository.findAllById(candidateIds);
        if (candidates.size() != candidateIds.size()) {
            throw new RuntimeException("Some candidates were not found.");
        }

        for (CandidatesEntity candidate : candidates) {
            if (!candidate.getParty().getId().equals(party.getId())) {
                throw new RuntimeException("Candidate " + candidate.getId() + " does not belong to party " + party.getId());
            }
        }

        return user;
    }


}
