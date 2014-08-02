library ieee;
use ieee.std_logic_1164.all;

entity process_outer is
    port (clk : in std_logic;
        in1 : in std_logic;
        in2 : in std_logic;
        out1 : out std_logic;
        out2 : out std_logic;
        out3 : out std_logic);
end;

architecture behavioral of process_outer is
    constant CLK_TRIGGER_VALUE : std_logic := '1';
    constant IN1_CONSTANT : std_logic := '1';
begin
    process (clk, rst) is
    begin
        if clk'event and clk = CLK_TRIGGER_VALUE then
            if in1 = IN1_CONSTANT then
                -- Missing else branch.
                out1 <= in2;
            end if;
            if in1 = IN1_CONSTANT then
                out2 <= in2;
            else
                -- Assigned in every branch.
                out2 <= not in2; -- Assigned in one branch (out of two possible).
                out3 <= in2;
            end if; -- Assignment in every branch, but depends on previous value.
            out4 <= not out4; -- Assignment in every branch and combination of other signals.
            out5 <= out4 and in2;
        end if;
    end process ;
    rst <= '1';
end;

